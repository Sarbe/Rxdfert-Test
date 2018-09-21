package com.pos.retailer.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.CustomerDetails;
import com.pos.retailer.model.InventoryTransaction;
import com.pos.retailer.model.PaymentDetails;
import com.pos.retailer.model.PaymentHistory;
import com.pos.retailer.model.Product;
import com.pos.retailer.model.SalesOrder;
import com.pos.retailer.model.SalesOrderDetails;
import com.pos.retailer.model.SalesOrderDto;
import com.pos.retailer.repository.ProductRepository;
import com.pos.retailer.repository.SalesOrderDetailRepository;
import com.pos.retailer.repository.SalesOrderRepository;
import com.pos.retailer.service.CustomerService;
import com.pos.retailer.service.PaymentHistoryService;
import com.pos.retailer.service.ProductService;
import com.pos.retailer.service.SalesOrderService;
import com.pos.retailer.service.SequenceGeneratorService;

@Service
@Transactional
public class SalesOrderServiceImpl implements SalesOrderService {
	Logger logger = LoggerFactory.getLogger(SalesOrderServiceImpl.class);

	@Autowired
	private SalesOrderRepository salesOrderRepository;

	@Autowired
	private SalesOrderDetailRepository salesOrderDetailRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	SequenceGeneratorService sequenceGenerator;

	@Autowired
	PaymentHistoryService paymentHistoryService;

	@Autowired
	ProductService productService;

	@Autowired
	CustomerService customerService;

	@Autowired
	private SequenceGeneratorService sequenceGeneratorService;

	@Override
	public SalesOrder generateOrderId() {

		String[] orders = salesOrderRepository.findEmptyOrders();
		if (orders.length > 0)
			return salesOrderRepository.findByOrderId(orders[0]).get();

		Long seq = sequenceGenerator.getNextValue("salesid");
		String orderId = "OD" + StringUtils.leftPad(seq.toString(), 10, "0");

		SalesOrder sales = new SalesOrder(orderId);
		sales.setOrderSts(AppConstant.ORDER_NEW);
		sales.setSettled(false);
		return salesOrderRepository.save(sales);
	}

	@Override
	public List<SalesOrder> getAllSalesOrders() {
		logger.info("SalesOrderServiceImpl.getAllSalesOrders()");
		return salesOrderRepository.findAllByOrderByOrderIdDesc();
	}

	@Override
	public List<SalesOrder> getAllSalesOrdersByStatus(List<String> status) {

		if (status.contains("ALL"))
			return getAllSalesOrders();
		else
			return salesOrderRepository.findByOrderStsInOrderByActivityDtDesc(status);
	}

	@Override
	public SalesOrder getSalesOrderById(String id) throws GenericException {
		return salesOrderRepository.findByOrderId(id)
				.orElseThrow(() -> new GenericException("order details not found."));
	}

	@Override
	public SalesOrderDto getFullSalesDetails(String id) throws GenericException {
		SalesOrderDto sdto = new SalesOrderDto();

		SalesOrder salesOrder = getSalesOrderById(id);
		sdto.setSales(salesOrder);

		List<SalesOrderDetails> salesOrderDtls = salesOrderDetailRepository.findByOrderId(id);
		sdto.setSalesDetails(salesOrderDtls);

		return sdto;

	}

	@Override
	public String getSalesOrderStatus(String id) throws GenericException {
		SalesOrder salesOrder = getSalesOrderById(id);
		return salesOrder.getOrderSts();
	}

	@Override
	public CustomerDetails getCustomerDetailsByContactNbr(String contactNbr) {
		return customerService.getCustomerByContactNumber(contactNbr, AppConstant.CUSTOMER);
	}

	@Override
	public List<CustomerDetails> getUniqueCustomerDetails() {
		return customerService.getUniqueDetails(AppConstant.CUSTOMER);
	}

	@Override
	public SalesOrder saveSalesOrder(SalesOrder salesOrder) throws GenericException {

		Optional<SalesOrder> optSalesOrder = Optional.empty();
		if (salesOrder.getOrderId() != null) // IF EXISTING ORDER
			optSalesOrder = salesOrderRepository.findById(salesOrder.getOrderId());

		if (!optSalesOrder.isPresent() || (optSalesOrder.isPresent()
				&& !optSalesOrder.get().getOrderSts().equals(AppConstant.ORDER_CONFIRMED))) {

			salesOrder.setOrderSts(AppConstant.ORDER_SAVED);
			salesOrder.setActivityDt(LocalDateTime.now());

			salesOrder = salesOrderRepository.save(salesOrder);

		} else {
			throw new GenericException("Cannot save details as Order has already been confirmed.");
		}

		return salesOrder;

	}

	@Override
	public SalesOrder confirmSalesOrder(String orderId, SalesOrder salesOrder) throws GenericException {
		SalesOrder dbSalesOrder = getSalesOrderById(orderId);
		if (dbSalesOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed");

		salesOrder.setPartyName(StringUtils.trimToEmpty(salesOrder.getPartyName()).toUpperCase());
		// check if customer data present
		CustomerDetails customer = new CustomerDetails(AppConstant.CUSTOMER, salesOrder.getPartyName(),
				salesOrder.getContactNbr(), salesOrder.getGstinNumber(), salesOrder.getAddress());
		if (customer.checkEmpty()) {
			throw new GenericException("Please enter Customer Details.");
		}

		// InventoryTransaction transc = null;
		// List<InventoryTransaction> transactions = new ArrayList<>();

		List<SalesOrderDetails> salesOrderDetails = salesOrderDetailRepository.findByOrderId(orderId);
		if (salesOrderDetails == null || salesOrderDetails.isEmpty()) {
			throw new GenericException("Please add some item to order first.");
		}

		// Amount Calculation
		double orderSubTotal = 0;
		double orderGrandTotal = 0;
		double orderTotalTax = 0;
		double mrpTotalTax = 0;

		for (SalesOrderDetails salesOrderDtl : salesOrderDetails) {

			orderGrandTotal += salesOrderDtl.getTotalAmount();
			orderSubTotal += salesOrderDtl.getSubTotal();
			orderTotalTax += salesOrderDtl.getTax();

			mrpTotalTax += salesOrderDtl.getMrp() * salesOrderDtl.getQty();

			// Product inventory =
			// productRepository.findByBarcode(salesOrderDtl.getBarcode()).get();
			// inventory.decreaseStockQty(salesOrderDtl.getQty());// decrease
			// productRepository.save(inventory);

			// transc = new InventoryTransaction(salesOrderDtl.getProductName(),
			// salesOrderDtl.getBarcode(),
			// salesOrderDtl.getUom(), AppConstant.STOCK_OUT, salesOrderDtl.getQty(),
			// dbSalesOrder.getPartyName(),
			// dbSalesOrder.getGstinNumber(), "Stocking Out for sales : " +
			// dbSalesOrder.getOrderId());
			//
			// transactions.add(transc);
		}

		salesOrder.setGrandTotal(orderGrandTotal);
		salesOrder.setOutstandingAmount(orderGrandTotal);
		salesOrder.setSubTotal(orderSubTotal);
		salesOrder.setTaxAmount(orderTotalTax);
		salesOrder.setTotalMrp(mrpTotalTax);
		// salesOrder.setDiscount(mrpTotalTax - orderGrandTotal);

		salesOrder.setOrderSts(AppConstant.ORDER_SAVED);

		salesOrder.setSettled(false);

		// save to inventory Transaction
		// productService.addInventoryTransactions(transactions);

		// save customer Details
		customerService.saveCustomer(customer);

		// save
		return salesOrderRepository.save(salesOrder);

		// SalesOrderDto sdto = new SalesOrderDto();
		// sdto.setSales(dbSalesOrder);
		// sdto.setSalesDetails(salesOrderDetails);
		//
		// return sdto;
	}

	@Override
	public SalesOrderDto receivePayment(String orderId, PaymentDetails payment) throws GenericException {
		SalesOrder dbSalesOrder = getSalesOrderById(orderId);

		// to be received only if in SAVED status.
		// if (dbSalesOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
		// throw new GenericException("Order has already been confirmed");

		if (StringUtils.trimToEmpty(payment.getPaymentMode()).isEmpty()) {
			payment.setPaymentMode(AppConstant.PAY_LATER);
		}

		if (dbSalesOrder.getOutstandingAmount() == 0 || dbSalesOrder.isSettled()) {
			throw new GenericException("No Payment due.");
		}

		double instantDisc = payment.getInstantDiscount();
		if (instantDisc > dbSalesOrder.getOutstandingAmount()) {
			throw new GenericException("Discount can not be more than outstanding Amount");
		}

		if ((dbSalesOrder.getOutstandingAmount() - instantDisc) < payment.getPaidAmount()) {
			throw new GenericException("Paid Amount cannot be greater than outstanding amount.");
		}

		dbSalesOrder.setDiscount(instantDisc);
		// dbSalesOrder.setOutstandingAmount(dbSalesOrder.getOutstandingAmount() -
		// instantDisc);

		dbSalesOrder.setOutstandingAmount(dbSalesOrder.getOutstandingAmount() - payment.getPaidAmount() - instantDisc);
		// String salePaymentType =
		// StringUtils.stripToEmpty(dbSalesOrder.getPaymentType());
		dbSalesOrder.setPaymentType(payment.getPaymentMode());

		if (dbSalesOrder.getOutstandingAmount() == 0) {
			dbSalesOrder.setSettled(true);
			payment.setPaymentMode(AppConstant.PAY_NOW);
		} else {
			dbSalesOrder.setSettled(false);
		}

		// if (payment.getPaymentMode().equals(AppConstant.PAY_NOW) ||
		// salePaymentType.equals(AppConstant.PAY_NOW)) {
		// dbSalesOrder.setPaymentType(payment.getPaymentMode());
		// dbSalesOrder.setSettled(true);
		// } else if (payment.getPaymentMode().equals(AppConstant.PAY_LATER)
		// || salePaymentType.equals(AppConstant.PAY_LATER)) {
		// dbSalesOrder.setPaymentType(payment.getPaymentMode());
		//
		// if (dbSalesOrder.getOutstandingAmount() == 0) {
		// dbSalesOrder.setSettled(true);
		// } else {
		// dbSalesOrder.setSettled(false);
		// }
		//
		// }

		// set status
		dbSalesOrder.setOrderSts(AppConstant.ORDER_CONFIRMED);

		// save
		PaymentHistory history = new PaymentHistory(AppConstant.SALES, dbSalesOrder.getOrderId(),
				payment.getPaymentMode(), dbSalesOrder.getGrandTotal(), payment.getPaidAmount(),
				dbSalesOrder.getOutstandingAmount());
		paymentHistoryService.savePaymentDetails(history);

		// update stock
		InventoryTransaction transc = null;
		List<InventoryTransaction> transactions = new ArrayList<>();
		List<SalesOrderDetails> salesOrderDetails = salesOrderDetailRepository.findByOrderId(orderId);
		for (SalesOrderDetails salesOrderDtl : salesOrderDetails) {

			Product inventory = productRepository.findByBarcode(salesOrderDtl.getBarcode()).get();
			inventory.decreaseStockQty(salesOrderDtl.getQty());// decrease
			productRepository.save(inventory);

			transc = new InventoryTransaction(salesOrderDtl.getProductName(), salesOrderDtl.getBarcode(),
					salesOrderDtl.getUom(), AppConstant.STOCK_OUT, salesOrderDtl.getQty(), dbSalesOrder.getPartyName(),
					dbSalesOrder.getGstinNumber(), "Stocking Out for sales : " + dbSalesOrder.getOrderId());

			transactions.add(transc);
		}

		// save to inventory Transaction
		productService.addInventoryTransactions(transactions);

		// Create Invoice on first payment
		if (StringUtils.isEmpty(dbSalesOrder.getReceiptNumber())) {
			Long seq = sequenceGeneratorService.getNextValue("invoiceNbr");
			String receiprtNbr = "INV/1" + StringUtils.leftPad(seq.toString(), 10, "0");
			dbSalesOrder.setReceiptNumber(receiprtNbr);
			salesOrderRepository.save(dbSalesOrder);
		}

		dbSalesOrder = salesOrderRepository.save(dbSalesOrder);

		SalesOrderDto sdto = new SalesOrderDto();
		sdto.setSales(dbSalesOrder);
		sdto.setSalesDetails(salesOrderDetails);

		return sdto;

	}

	@Override
	public SalesOrder updatePaymentDetails(String orderId, PaymentDetails payment) throws GenericException {
		SalesOrder dbSalesOrder = getSalesOrderById(orderId);

		// to be received only if in CONFIRMED status.
		if (!dbSalesOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order is not yet confirmed");

		if (dbSalesOrder.getOutstandingAmount() == 0 || dbSalesOrder.isSettled()) {
			throw new GenericException("No Payment due.");
		} else if (dbSalesOrder.getOutstandingAmount() < payment.getPaidAmount()) {
			throw new GenericException("Paid Amount cannot be greater than outstanding amount.");
		}

		dbSalesOrder.setOutstandingAmount(dbSalesOrder.getOutstandingAmount() - payment.getPaidAmount());

		if (dbSalesOrder.getOutstandingAmount() == 0) {
			dbSalesOrder.setSettled(true);
		} else {
			dbSalesOrder.setSettled(false);
		}

		// save
		PaymentHistory history = new PaymentHistory(AppConstant.SALES, dbSalesOrder.getOrderId(),
				payment.getPaymentMode(), dbSalesOrder.getGrandTotal(), payment.getPaidAmount(),
				dbSalesOrder.getOutstandingAmount());
		paymentHistoryService.savePaymentDetails(history);

		return salesOrderRepository.save(dbSalesOrder);

	}

	@Override
	public void deleteSalesOrder(String orderId) throws GenericException {
		SalesOrder salesOrder = getSalesOrderById(orderId);

		/*
		 * // update stock if confirmed order if
		 * (salesOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED)) {
		 * List<SalesOrderDetails> salesOrderDetails =
		 * salesOrderDetailRepository.findByOrderId(orderId); if (salesOrderDetails !=
		 * null && !salesOrderDetails.isEmpty()) {
		 * 
		 * for (SalesOrderDetails salesOrderDtl : salesOrderDetails) { Product inventory
		 * = productRepository.findByBarcode(salesOrderDtl.getBarcode()).get();
		 * inventory.increaseStockQty(salesOrderDtl.getQty());
		 * productRepository.save(inventory);
		 * 
		 * } } }
		 * 
		 * salesOrderRepository.deleteById(orderId); // delete order details
		 * salesOrderDetailRepository.deleteAllByOrderId(orderId);
		 */

		if (!salesOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED)) {
			salesOrderRepository.deleteById(orderId); // delete order details
			salesOrderDetailRepository.deleteAllByOrderId(orderId);
		} else {
			throw new GenericException("Cannot delete as Order is already confirmed.");
		}

	}

	@Override
	public SalesOrderDto reCalc(String orderId) throws GenericException {

		SalesOrder sorder = salesOrderRepository.findByOrderId(orderId).get();
		List<SalesOrderDetails> salesOrderDetails = salesOrderDetailRepository.findByOrderId(orderId);
		if (salesOrderDetails == null || salesOrderDetails.isEmpty()) {
			throw new GenericException("No details found");
		}

		// Amount Calculation
		double orderSubTotal = 0;
		double orderGrandTotal = 0;
		double orderTotalTax = 0;
		double mrpTotalTax = 0;

		for (SalesOrderDetails salesOrderDtl : salesOrderDetails) {
			Product product = productService.getProductByBarcode(salesOrderDtl.getBarcode());
			salesOrderDtl.calculateAmount(product);
			// save order
			salesOrderDetailRepository.save(salesOrderDtl);

			// recalculate for sales order
			orderGrandTotal += salesOrderDtl.getTotalAmount();
			orderSubTotal += salesOrderDtl.getSubTotal();
			orderTotalTax += salesOrderDtl.getTax();
			mrpTotalTax += salesOrderDtl.getMrp() * salesOrderDtl.getQty();

		}

		sorder.setGrandTotal(orderGrandTotal);
		sorder.setOutstandingAmount(orderGrandTotal);
		sorder.setSubTotal(orderSubTotal);
		sorder.setTaxAmount(orderTotalTax);
		sorder.setTotalMrp(mrpTotalTax);

		sorder = salesOrderRepository.save(sorder);

		SalesOrderDto sdto = new SalesOrderDto();
		sdto.setSales(sorder);
		sdto.setSalesDetails(salesOrderDetailRepository.findByOrderId(orderId));

		return sdto;
	}

}
