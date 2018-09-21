package com.pos.retailer.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.Product;
import com.pos.retailer.model.SalesOrder;
import com.pos.retailer.model.SalesOrderDetails;
import com.pos.retailer.repository.SalesOrderDetailRepository;
import com.pos.retailer.repository.SalesOrderRepository;
import com.pos.retailer.service.CommonOrderService;
import com.pos.retailer.service.ProductService;
import com.pos.retailer.service.SalesOrderDetailsService;

@Service
public class SalesOrderDetailsServiceImpl implements SalesOrderDetailsService {

	@Autowired
	private SalesOrderRepository salesOrderRepository;

	@Autowired
	private CommonOrderService commonOrderService;

	@Autowired
	private SalesOrderDetailRepository salesOrderDetailRepository;

	@Autowired
	ProductService productService;

	@Override
	public SalesOrderDetails getOrderDetailById(Long orderDtlId) throws GenericException {
		return salesOrderDetailRepository.findById(orderDtlId)
				.orElseThrow(() -> new GenericException("Data not found for orderDetail: " + orderDtlId));
	}

	@Override
	public List<SalesOrderDetails> getOrderDetailsByOrderId(String orderId) {
		return salesOrderDetailRepository.findByOrderId(orderId);
	}

	// unused
	@Override
	public SalesOrderDetails addOrderDetail(String orderId, String barcode) throws GenericException {

		SalesOrder salesOrder = commonOrderService.getSalesOrderByOrderId(orderId);
		if (salesOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed.");

		// get product by barcode
		Product product = productService.getProductByBarcode(barcode);

		if (ObjectUtils.isEmpty(product)) {
			throw new GenericException("No Product is mapped to this barcode.");
		}

		if (!product.isAvailability() || product.getStockQty() < 1) {
			throw new GenericException("Product is not available");
		}

		SalesOrderDetails dbSaleOrderDtl = salesOrderDetailRepository.findByOrderIdAndBarcode(orderId, barcode);

		if (dbSaleOrderDtl == null) {
			dbSaleOrderDtl = new SalesOrderDetails(barcode, product, 1, orderId); // Initial qty 1
		} else {

			if (dbSaleOrderDtl.getQty() == product.getStockQty())
				throw new GenericException("No more item can be added for product " + dbSaleOrderDtl.getProductName());

			dbSaleOrderDtl.setQty(dbSaleOrderDtl.getQty() + 1); // increase qty by 1
		}

		// order detail Price Calculation
		dbSaleOrderDtl.calculateAmount(product);
		// order Price Calculation
		List<SalesOrderDetails> salesOrderDetails = salesOrderDetailRepository.findByOrderId(salesOrder.getOrderId());
		salesOrder.calculateOrderPrice(salesOrderDetails);

		salesOrderRepository.save(salesOrder);

		return salesOrderDetailRepository.save(dbSaleOrderDtl);
	}

	@Override
	public SalesOrderDetails saveProductByBarcodeAndQty(String orderId, String barcode, int qty)
			throws GenericException {

		// verify order
		SalesOrder salesOrder = commonOrderService.getSalesOrderByOrderId(orderId);
		if (salesOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed.");

		// verify Product barcode
		Product product = productService.getProductByBarcode(barcode);
		if (ObjectUtils.isEmpty(product)) {
			throw new GenericException("No Product is mapped to this barcode.");
		}

		if (!product.isAvailability() || product.getStockQty() < 1) {
			throw new GenericException("Product is not available");
		}

		// verify input
		if (qty < 1)
			throw new GenericException("Quantity cannot be less than 1.");
		else if (qty > product.getStockQty())
			throw new GenericException("Insufficient stock for " + product.getProductName());

		// verify order details
		SalesOrderDetails saleOrderDtl = salesOrderDetailRepository.findByOrderIdAndBarcode(orderId, barcode);
		if (!ObjectUtils.isEmpty(saleOrderDtl)) {
			if ((saleOrderDtl.getQty() + qty) > product.getStockQty()) {
				throw new GenericException("Insufficient stock for " + saleOrderDtl.getProductName());
			}
			saleOrderDtl.increaseQty(qty);

		} else {
			// Add to order details
			saleOrderDtl = new SalesOrderDetails(barcode, product, qty, orderId);
		}

		// calculate order detail price
		saleOrderDtl.calculateAmount(product);
		saleOrderDtl = salesOrderDetailRepository.save(saleOrderDtl);

		// order Price Calculation
		List<SalesOrderDetails> salesOrderDetails = salesOrderDetailRepository.findByOrderId(salesOrder.getOrderId());
		salesOrder.calculateOrderPrice(salesOrderDetails);
		// save sale Order
		salesOrder.setOrderSts(AppConstant.ORDER_SAVED);
		salesOrderRepository.save(salesOrder);

		// save sale Order details
		return saleOrderDtl;
	}

	//unused
	@Override
	public SalesOrderDetails changeQtyByOne(String orderId, Long orderDtlId, String changeType)
			throws GenericException {

		SalesOrder salesOrder = commonOrderService.getSalesOrderByOrderId(orderId);
		if (salesOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed.");

		Optional<SalesOrderDetails> optSaleOrderDtl = salesOrderDetailRepository.findById(orderDtlId);

		if (optSaleOrderDtl.isPresent()) {
			SalesOrderDetails saleOrderDtl = optSaleOrderDtl.get();

			// get product by barcode
			Product product = productService.getProductByBarcode(saleOrderDtl.getBarcode());

			if (saleOrderDtl.getQty() == product.getStockQty() && changeType.equals(AppConstant.QTY_ADD))
				throw new GenericException("No more item can be added for product " + saleOrderDtl.getProductName());
			if (saleOrderDtl.getQty() == 1 && changeType.equals(AppConstant.QTY_SUB)) {
				throw new GenericException("Quantity cannot be less than 1.");
			}

			if (changeType.equals(AppConstant.QTY_ADD))
				saleOrderDtl.increaseQty(1);
			else if (changeType.equals(AppConstant.QTY_SUB))
				saleOrderDtl.decreaseQty(1);
			else
				return saleOrderDtl;
			// calculate order details amount
			saleOrderDtl.calculateAmount(product);
			saleOrderDtl = salesOrderDetailRepository.save(saleOrderDtl);
			
			// calculate order amount
			List<SalesOrderDetails> salesOrderDetails = salesOrderDetailRepository.findByOrderId(salesOrder.getOrderId());
			salesOrder.calculateOrderPrice(salesOrderDetails);

			salesOrderRepository.save(salesOrder);

			return saleOrderDtl;
		} else {
			throw new GenericException("Cannot perform the operation.");
		}

	}

	@Override
	public SalesOrderDetails changeQty(String orderId, Long orderDtlId, int qty) throws GenericException {

		SalesOrder salesOrder = commonOrderService.getSalesOrderByOrderId(orderId);
		if (salesOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed.");

		Optional<SalesOrderDetails> optSaleOrderDtl = salesOrderDetailRepository.findById(orderDtlId);

		if (optSaleOrderDtl.isPresent()) {
			SalesOrderDetails saleOrderDtl = optSaleOrderDtl.get();

			// get product by barcode
			Product product = productService.getProductByBarcode(saleOrderDtl.getBarcode());

			if (qty < 1)
				throw new GenericException("Quantity cannot be less than 1.");
			else if (qty > product.getStockQty())
				throw new GenericException("Insufficient stock for " + product.getProductName());

			saleOrderDtl.setQty(qty);

			// calculate order details amount
			saleOrderDtl.calculateAmount(product);
			saleOrderDtl = salesOrderDetailRepository.save(saleOrderDtl);
			// calculate order amount
			List<SalesOrderDetails> salesOrderDetails = salesOrderDetailRepository.findByOrderId(salesOrder.getOrderId());
			salesOrder.calculateOrderPrice(salesOrderDetails);

			salesOrderRepository.save(salesOrder);

			return saleOrderDtl;
		} else {
			throw new GenericException("Order Details not found");
		}

	}

//	private void calculateOrderPrice(SalesOrder salesOrder) {
//		List<SalesOrderDetails> salesOrderDetails = salesOrderDetailRepository.findByOrderId(salesOrder.getOrderId());
//		double orderSubTotal = 0;
//		double orderGrandTotal = 0;
//		double orderTotalTax = 0;
//		double mrpTotalTax = 0;
//
//		for (SalesOrderDetails salesOrderDtl : salesOrderDetails) {
//			orderGrandTotal += salesOrderDtl.getTotalAmount();
//			orderSubTotal += salesOrderDtl.getSubTotal();
//			orderTotalTax += salesOrderDtl.getTax();
//			mrpTotalTax += salesOrderDtl.getMrp() * salesOrderDtl.getQty();
//		}
//
//		salesOrder.setGrandTotal(orderGrandTotal);
//		salesOrder.setOutstandingAmount(orderGrandTotal);
//		salesOrder.setSubTotal(orderSubTotal);
//		salesOrder.setTaxAmount(orderTotalTax);
//		salesOrder.setTotalMrp(mrpTotalTax);
//	}

	@Override
	public void deleteOrderDetailById(String orderId, Long orderDtlId) throws GenericException {

		SalesOrder salesOrder = commonOrderService.getSalesOrderByOrderId(orderId);
		if (salesOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed.");

		Optional<SalesOrderDetails> optSaleOrderDtl = salesOrderDetailRepository.findById(orderDtlId);

		if (!optSaleOrderDtl.isPresent())
			throw new GenericException("Details not found.");

		// SalesOrderDetails salesOrderDtl = optSaleOrderDtl.get();
		salesOrderDetailRepository.deleteById(orderDtlId);

		// calculate order amount
		List<SalesOrderDetails> salesOrderDetails = salesOrderDetailRepository.findByOrderId(salesOrder.getOrderId());
		salesOrder.calculateOrderPrice(salesOrderDetails);

		salesOrderRepository.save(salesOrder);
	}
}
