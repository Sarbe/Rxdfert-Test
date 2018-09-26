package com.pos.retailer.service.Impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.CustomerDetails;
import com.pos.retailer.model.InventoryOrder;
import com.pos.retailer.model.InventoryOrderDetails;
import com.pos.retailer.model.InventoryOrderDto;
import com.pos.retailer.model.InventoryTransaction;
import com.pos.retailer.model.PaymentDetails;
import com.pos.retailer.model.PaymentHistory;
import com.pos.retailer.model.Product;
import com.pos.retailer.repository.InventoryOrderDetailRepository;
import com.pos.retailer.repository.InventoryOrderRepository;
import com.pos.retailer.repository.ProductRepository;
import com.pos.retailer.service.CustomerService;
import com.pos.retailer.service.InventoryOrderService;
import com.pos.retailer.service.PaymentHistoryService;
import com.pos.retailer.service.ProductService;
import com.pos.retailer.service.SequenceGeneratorService;

@Service
@Transactional
public class InventoryOrderServiceImpl implements InventoryOrderService {

	@Autowired
	private InventoryOrderRepository inventoryOrderRepository;

	@Autowired
	private InventoryOrderDetailRepository inventoryOrderDetailRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	PaymentHistoryService paymentHistoryService;

	@Autowired
	ProductService productService;

	@Autowired
	CustomerService customerService;

	@Autowired
	SequenceGeneratorService sequenceGeneratorService;

	@Override
	public InventoryOrder generateOrderId() {

		String[] orders = inventoryOrderRepository.findEmptyOrders();
		if (orders.length > 0)
			return inventoryOrderRepository.findByOrderId(orders[0]).get();

		Long seq = sequenceGeneratorService.getNextValue("purchaseid");
		String orderId = "RCV" + StringUtils.leftPad(seq.toString(), 10, "0");

		InventoryOrder order = new InventoryOrder(orderId);
		order.setOrderSts(AppConstant.ORDER_NEW);
		return inventoryOrderRepository.save(order);
	}

	@Override
	public List<InventoryOrder> getAllInvOrders() {
		return inventoryOrderRepository.findAllByOrderByOrderIdDesc();
	}

	@Override
	public List<InventoryOrder> getAllInvOrdersByStatus(List<String> status) {

		if (status.contains("ALL"))
			return inventoryOrderRepository.findAll();
		else
			return inventoryOrderRepository.findByOrderStsInOrderByActivityDtDesc(status);
	}

	@Override
	public InventoryOrder getInvOrderById(String id) throws GenericException {
		return inventoryOrderRepository.findById(id)
				.orElseThrow(() -> new GenericException("order details not found."));
	}

	@Override
	public InventoryOrderDto getFullInvOrderById(String invOrderid) throws GenericException {
		InventoryOrder optInv = inventoryOrderRepository.findById(invOrderid)
				.orElseThrow(() -> new GenericException("order details not found."));

		InventoryOrderDto invDto = new InventoryOrderDto();
		invDto.setPurchase(optInv);

		List<InventoryOrderDetails> invOrderDtls = inventoryOrderDetailRepository.findByOrderId(invOrderid);
		invDto.setPurchaseDetails(invOrderDtls);

		return invDto;

	}

	@Override
	public String getInvOrderStatus(String id) throws GenericException {
		InventoryOrder invOrder = getInvOrderById(id);
		return invOrder.getOrderSts();
	}

	@Override
	public CustomerDetails getCustomerDetailsByContactNbr(String contactNbr) {
		return customerService.getCustomerByContactNumber(contactNbr, AppConstant.VENDOR);
	}

	@Override
	public List<CustomerDetails> getUniqueVendorDetails() {
		return customerService.getUniqueDetails(AppConstant.VENDOR);
	}

	@Override
	public InventoryOrder saveInvOrder(InventoryOrder invOrder) throws GenericException {

		Optional<InventoryOrder> optInvOrder = inventoryOrderRepository.findByOrderIdAndOrderSts(invOrder.getOrderId(),
				AppConstant.ORDER_CONFIRMED);

		if (optInvOrder.isPresent())
			throw new GenericException("Order has already been confirmed.");

		invOrder.setOrderSts(AppConstant.ORDER_SAVED);
		invOrder.setActivityDt(LocalDateTime.now());

		invOrder = inventoryOrderRepository.save(invOrder);
		return invOrder;

	}

	@Override
	public InventoryOrder submitOrder(String orderId, InventoryOrder invOrder) throws GenericException {

		InventoryOrder dbInvOrder = getInvOrderById(orderId);
		if (dbInvOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed");

		CustomerDetails customer = new CustomerDetails(AppConstant.VENDOR, invOrder.getPartyName(),
				invOrder.getContactNbr(), invOrder.getGstinNumber(), "");

		if (customer.checkEmpty()) {
			throw new GenericException("Please enter vendor Details.");
		}


		List<InventoryOrderDetails> invOrderDetails = inventoryOrderDetailRepository.findByOrderId(orderId);

		if (invOrderDetails == null || invOrderDetails.isEmpty()) {
			throw new GenericException("Please add some item to order first.");
		}

		
		invOrder.calculateOrderAmount(invOrderDetails); // not required but still...
		
		invOrder.setOrderSts(AppConstant.ORDER_SAVED);
		invOrder.setSettled(false);

		// save
		inventoryOrderRepository.save(invOrder);

		// save customer Details
		customerService.saveCustomer(customer);

		return invOrder;

	}

	@Override
	public InventoryOrder receivePaymentDetails(String orderId, PaymentDetails payment) throws GenericException {
		InventoryOrder dbInvOrder = getInvOrderById(orderId);

		if (dbInvOrder.getOutstandingAmount() == 0 || dbInvOrder.isSettled()) {
			throw new GenericException("No Payment due.");
		} else if (dbInvOrder.getOutstandingAmount() < payment.getPaidAmount()) {
			throw new GenericException("Paid Amount cannot be greater than outstanding amount.");
		}
		
		dbInvOrder.setOutstandingAmount(dbInvOrder.getOutstandingAmount() - payment.getPaidAmount());

		if (dbInvOrder.getOutstandingAmount() == 0) {
			dbInvOrder.setSettled(true);
			payment.setPaymentType(AppConstant.PAY_NOW);
		} else {
			dbInvOrder.setSettled(false);
			payment.setPaymentType(AppConstant.PAY_LATER);
		}

		dbInvOrder.setPaymentType(payment.getPaymentType());

		// set status
		dbInvOrder.setOrderSts(AppConstant.ORDER_CONFIRMED);

		// save
		PaymentHistory history = new PaymentHistory(AppConstant.PURCHASE, dbInvOrder.getOrderId().toString(),
				payment.getPaymentType(),payment.getPaymentMode(), dbInvOrder.getGrandTotal(), payment.getPaidAmount(),
				dbInvOrder.getOutstandingAmount());
		paymentHistoryService.savePaymentDetails(history);

		
		// update stock
		InventoryTransaction transc = null;
		List<InventoryTransaction> transactions = new ArrayList<>();

		List<InventoryOrderDetails> invOrderDetails = inventoryOrderDetailRepository.findByOrderId(orderId);

		for (InventoryOrderDetails invOrderDtl : invOrderDetails) {
			Product inventory = productRepository.findByBarcode(invOrderDtl.getBarcode()).get();
			inventory.increaseStockQty(invOrderDtl.getQty()); // increase
			
			// if required ???
			// inventory.setBuyPrice(invOrderDtl.getBuyPrice());// update buy price
			// inventory.setMaxRetailPrice(invOrderDtl.getMaxRetailPrice());
			productRepository.save(inventory);

			transc = new InventoryTransaction(invOrderDtl.getProductName(), invOrderDtl.getBarcode(),
					invOrderDtl.getUom(), AppConstant.STOCK_IN, invOrderDtl.getQty(), dbInvOrder.getPartyName(),
					dbInvOrder.getGstinNumber(), "Stocking in for order : " + dbInvOrder.getOrderId());

			transactions.add(transc);

		}

		// save to inventory Transaction
		productService.addInventoryTransactions(transactions);
		
		dbInvOrder.setActivityDt(LocalDateTime.now()); // payment time
		return inventoryOrderRepository.save(dbInvOrder);
	}

	@Override
	public InventoryOrder updatePaymentDetails(String orderId, PaymentDetails payment) throws GenericException {
		InventoryOrder dbInvOrder = getInvOrderById(orderId);

		// to be received only if in CONFIRMED status.
		if (!dbInvOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order is not yet confirmed");

		if (dbInvOrder.getOutstandingAmount() == 0 || dbInvOrder.isSettled()) {
			throw new GenericException("No Payment due.");
		} else if (dbInvOrder.getOutstandingAmount() < payment.getPaidAmount()) {
			throw new GenericException("Paid Amount cannot be greater than outstanding amount.");
		}

		dbInvOrder.setOutstandingAmount(dbInvOrder.getOutstandingAmount() - payment.getPaidAmount());

		// save
		PaymentHistory history = new PaymentHistory(AppConstant.PURCHASE, dbInvOrder.getOrderId().toString(),
				AppConstant.PAY_LATER, payment.getPaymentMode(), dbInvOrder.getGrandTotal(), payment.getPaidAmount(),
				dbInvOrder.getOutstandingAmount());
		paymentHistoryService.savePaymentDetails(history);

		return inventoryOrderRepository.save(dbInvOrder);

	}

	@Override
	public void deleteInvOrder(String orderId) throws GenericException {
		InventoryOrder invOrder = getInvOrderById(orderId);
		
		if (!invOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED)) {
			inventoryOrderRepository.deleteById(orderId); // delete order details
			inventoryOrderDetailRepository.deleteAllByOrderId(orderId);
		} else {
			throw new GenericException("Cannot delete as Order has already Confirmed.");
		}

	}

	@Override
	public InventoryOrderDto recal(String orderId) {
		InventoryOrder invOrder = inventoryOrderRepository.findByOrderId(orderId).get();

		List<InventoryOrderDetails> invOrderDetails = inventoryOrderDetailRepository.findByOrderId(orderId);

		double orderTotal = 0;
		for (InventoryOrderDetails invOrderDtl : invOrderDetails) {
			invOrderDtl.calculateAmount();
			inventoryOrderDetailRepository.save(invOrderDtl);

			orderTotal += invOrderDtl.getTotalAmount();
		}

		invOrder.setGrandTotal(orderTotal);
		invOrder.setOutstandingAmount(invOrder.getGrandTotal());
		
		// check if any transaction has done
		double paidAmount = paymentHistoryService.getTotalPayment(orderId, AppConstant.PURCHASE);
		invOrder.setOutstandingAmount(invOrder.getOutstandingAmount() - paidAmount);

		// save
		invOrder = inventoryOrderRepository.save(invOrder);

		// verify op
		InventoryOrderDto invdto = new InventoryOrderDto();
		invdto.setPurchase(invOrder);
		invdto.setPurchaseDetails(inventoryOrderDetailRepository.findByOrderId(orderId));

		return invdto;
	}
}
