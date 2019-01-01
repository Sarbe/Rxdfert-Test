package com.pos.retailer.service.Impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.InventoryOrder;
import com.pos.retailer.model.InventoryOrderDetails;
import com.pos.retailer.model.Product;
import com.pos.retailer.repository.InventoryOrderDetailRepository;
import com.pos.retailer.repository.InventoryOrderRepository;
import com.pos.retailer.repository.ProductRepository;
import com.pos.retailer.service.CommonOrderService;
import com.pos.retailer.service.InventoryOrderDetailsService;

@Service
@Transactional
public class InventoryOrderDetailsServiceImpl implements InventoryOrderDetailsService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private InventoryOrderDetailRepository inventoryOrderDetailRepository;

	@Autowired
	private InventoryOrderRepository inventoryOrderRepository;

	@Autowired
	CommonOrderService commonOrderService;

	/*
	 * @Autowired private InventoryOrderService inventoryOrderService;
	 */

	@Override
	public InventoryOrderDetails getOrderDetailById(Long orderDtlId) throws GenericException {
		return inventoryOrderDetailRepository.findById(orderDtlId)
				.orElseThrow(() -> new GenericException("Data not found for orderDetail: " + orderDtlId));
	}

	@Override
	public List<InventoryOrderDetails> getOrderDetailsByOrderId(String orderId) {
		return inventoryOrderDetailRepository.findByOrderId(orderId);
	}

	@Override
	public InventoryOrderDetails addOrderDetail(String orderId, InventoryOrderDetails orderDetail)
			throws GenericException {

		orderDetail.setOrderId(orderId);

		InventoryOrder invOrder = commonOrderService.getInventoryOrderByOrderId(orderId);
		if (invOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed.");

		// check if valid product by barcode and product Name
		Optional<Product> optionalProduct = productRepository.findByBarcodeAndProductName(orderDetail.getBarcode(),
				orderDetail.getProductName());
		if (!optionalProduct.isPresent()) {
			throw new GenericException("Product - " + orderDetail.getProductName() + " is not a valid entry.");
		}

		// input validation
		if (orderDetail.getQty() < 1)
			throw new GenericException("Quantity cannot be less than 1.");

		///////////////////

		// check if details is already added
		InventoryOrderDetails dbOrderDetail = inventoryOrderDetailRepository.findByOrderIdAndBarcode(orderId,
				orderDetail.getBarcode());

		if (dbOrderDetail != null) { // if present
			dbOrderDetail.increaseQty(orderDetail.getQty());
			dbOrderDetail.setMaxRetailPrice(optionalProduct.get().getMaxRetailPrice());
			dbOrderDetail.setBuyPrice(orderDetail.getBuyPrice()); // if there is any change in buying price

			// Price Calculation for order detail
			dbOrderDetail.calculateAmount();
			dbOrderDetail = inventoryOrderDetailRepository.save(dbOrderDetail);

			// Price Calculation for order
			List<InventoryOrderDetails> invOrderDetails = getOrderDetailsByOrderId(invOrder.getOrderId());
			invOrder.calculateOrderAmount(invOrderDetails);
			inventoryOrderRepository.save(invOrder);

			return dbOrderDetail;
		} else {

			orderDetail.setMaxRetailPrice(optionalProduct.get().getMaxRetailPrice());
			orderDetail.setBuyPrice(orderDetail.getBuyPrice()); // if there is any change in buying price

			// Price Calculation for order detail
			orderDetail.calculateAmount();
			orderDetail = inventoryOrderDetailRepository.save(orderDetail);
			
			// Price Calculation for order
			List<InventoryOrderDetails> invOrderDetails = getOrderDetailsByOrderId(invOrder.getOrderId());
			invOrder.calculateOrderAmount(invOrderDetails);
			return orderDetail;
		}

	}

	/*private void calculateOrderAmount(InventoryOrder invOrder) {
		List<InventoryOrderDetails> invOrderDetails = getOrderDetailsByOrderId(invOrder.getOrderId());
		double orderTotal = 0;
		for (InventoryOrderDetails invOrderDtl : invOrderDetails) {
			orderTotal += invOrderDtl.getTotalAmount();
		}
		
		invOrder.setGrandTotal(orderTotal);
		invOrder.setOutstandingAmount(orderTotal);

	}*/

	//unused
	@Override
	public InventoryOrderDetails changeQtyByOne(String orderId, Long orderDtlId, String changeType)
			throws GenericException {

		InventoryOrder invOrder = commonOrderService.getInventoryOrderByOrderId(orderId);
		if (invOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed.");

		InventoryOrderDetails dbOrderDetail = inventoryOrderDetailRepository.findByOrderDetailIdAndOrderId(orderDtlId,
				orderId);

		if (dbOrderDetail == null) // if order details not present
			throw new GenericException("Details not found.");

		if (changeType.equals(AppConstant.QTY_SUB) && dbOrderDetail.getQty() == 1) {
			throw new GenericException("Quantity cannot be less than 1.");
		}

		if (changeType.equals(AppConstant.QTY_ADD))
			dbOrderDetail.increaseQty(1);
		else if (changeType.equals(AppConstant.QTY_SUB))
			dbOrderDetail.decreaseQty(1);
		else
			return dbOrderDetail;

		// Order Detail Price Calculation
		dbOrderDetail.calculateAmount();
		dbOrderDetail = inventoryOrderDetailRepository.save(dbOrderDetail);
		// order price calculation
		List<InventoryOrderDetails> invOrderDetails = getOrderDetailsByOrderId(invOrder.getOrderId());
		invOrder.calculateOrderAmount(invOrderDetails);
		inventoryOrderRepository.save(invOrder);
		
		return dbOrderDetail;

	}
	
	@Override
	public InventoryOrderDetails changeQty(String orderId, Long orderDtlId, int qty)
			throws GenericException {

		InventoryOrder invOrder = commonOrderService.getInventoryOrderByOrderId(orderId);
		if (invOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed.");

		Optional<InventoryOrderDetails> optDbOrderDetail = inventoryOrderDetailRepository.findByOrderDetailId(orderDtlId);

		if (!optDbOrderDetail.isPresent()) // if order details not present
			throw new GenericException("Details not found.");

		InventoryOrderDetails dbOrderDetail = optDbOrderDetail.get();
		
		if (qty < 1) {			
			throw new GenericException("Quantity cannot be less than 1.");
		}

		dbOrderDetail.setQty(qty);
		// Order Detail Price Calculation
		dbOrderDetail.calculateAmount();
		dbOrderDetail = inventoryOrderDetailRepository.save(dbOrderDetail);
		// order price calculation
		List<InventoryOrderDetails> invOrderDetails = getOrderDetailsByOrderId(invOrder.getOrderId());
		invOrder.calculateOrderAmount(invOrderDetails);
		inventoryOrderRepository.save(invOrder);
		
		return dbOrderDetail;

	}

	@Override
	public void deleteOrderDetailById(String orderId, Long orderDtlid) throws GenericException {

		InventoryOrder invOrder = commonOrderService.getInventoryOrderByOrderId(orderId);
		if (invOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Can not delete as Order is already confirmed.");

		InventoryOrderDetails dbOrderDetail = inventoryOrderDetailRepository.findByOrderDetailIdAndOrderId(orderDtlid,
				orderId);

		if (dbOrderDetail == null) // if order details not present
			throw new GenericException("Details not found.");

		inventoryOrderDetailRepository.deleteById(orderDtlid);

		// order price calculation
		List<InventoryOrderDetails> invOrderDetails = getOrderDetailsByOrderId(invOrder.getOrderId());
		invOrder.calculateOrderAmount(invOrderDetails);
		inventoryOrderRepository.save(invOrder);

	}
}
