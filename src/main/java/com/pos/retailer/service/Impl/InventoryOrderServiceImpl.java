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
	ProductRepository inventoryRepository;

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
		
		if(status.contains("ALL"))
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
		/*
		 * List<VendorDetails> vendors = new ArrayList<>(); if (datas != null &&
		 * !datas.isEmpty())
		 * 
		 * datas.forEach((data) -> { if (data != null && data.length > 0) { String
		 * partyNm = (data[0] == null) ? "" : data[0].toString(); String gstin =
		 * (data[1] == null) ? "" : data[1].toString(); if (!partyNm.isEmpty() &&
		 * !gstin.isEmpty()) vendors.add(new VendorDetails(partyNm, gstin)); } });
		 * return vendors;
		 */
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
	public InventoryOrder confirmInventoryOrder(String orderId, InventoryOrder invOrder) throws GenericException {

		InventoryOrder dbInvOrder = getInvOrderById(orderId);
		if (dbInvOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
			throw new GenericException("Order has already been confirmed");

		invOrder.setPartyName(StringUtils.trimToEmpty(invOrder.getPartyName()).toUpperCase());
		CustomerDetails customer = new CustomerDetails(AppConstant.VENDOR, invOrder.getPartyName(),
				invOrder.getContactNbr(), invOrder.getGstinNumber(), "");

		if (customer.checkEmpty()) {
			throw new GenericException("Please enter vendor Details.");
		}

		// InventoryTransaction transc = null;
		// List<InventoryTransaction> transactions = new ArrayList<>();

		List<InventoryOrderDetails> invOrderDetails = inventoryOrderDetailRepository.findByOrderId(orderId);

		if (invOrderDetails == null || invOrderDetails.isEmpty()) {
			throw new GenericException("Please add some item to order first.");
		}

		double orderTotal = 0;
		for (InventoryOrderDetails invOrderDtl : invOrderDetails) {
			orderTotal += invOrderDtl.getTotalAmount();
		}

		invOrder.setGrandTotal(orderTotal);
		invOrder.setOrderSts(AppConstant.ORDER_SAVED);
		invOrder.setOutstandingAmount(invOrder.getGrandTotal());
		invOrder.setSettled(false);

		// save
		inventoryOrderRepository.save(invOrder);

		// save to inventory Transaction
		// productService.addInventoryTransactions(transactions);

		// save customer Details
		customerService.saveCustomer(customer);

		return invOrder;

	}

	@Override
	public InventoryOrder receivePaymentDetails(String orderId, PaymentDetails payment) throws GenericException {
		InventoryOrder dbInvOrder = getInvOrderById(orderId);

		// to be received only if in SAVED status.
		// if (dbInvOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED))
		// throw new GenericException("Order has already been confirmed");

		if (dbInvOrder.getOutstandingAmount() == 0 || dbInvOrder.isSettled()) {
			throw new GenericException("No Payment due.");
		} else if (dbInvOrder.getOutstandingAmount() < payment.getPaidAmount()) {
			throw new GenericException("Paid Amount cannot be greater than outstanding amount.");
		}

		if (dbInvOrder.getOutstandingAmount() == payment.getPaidAmount()) {
			dbInvOrder.setPaymentType(AppConstant.PAY_NOW);
			dbInvOrder.setSettled(true);
		} else {
			dbInvOrder.setPaymentType(AppConstant.PAY_LATER);
			dbInvOrder.setSettled(false);
		}

		dbInvOrder.setOutstandingAmount(dbInvOrder.getOutstandingAmount() - payment.getPaidAmount());

		// set status
		dbInvOrder.setOrderSts(AppConstant.ORDER_CONFIRMED);

		// save
		PaymentHistory history = new PaymentHistory(AppConstant.PURCHASE, dbInvOrder.getOrderId().toString(),
				payment.getPaymentMode(), dbInvOrder.getGrandTotal(), payment.getPaidAmount(),
				dbInvOrder.getOutstandingAmount());
		paymentHistoryService.savePaymentDetails(history);

		InventoryTransaction transc = null;
		List<InventoryTransaction> transactions = new ArrayList<>();

		List<InventoryOrderDetails> invOrderDetails = inventoryOrderDetailRepository.findByOrderId(orderId);

		for (InventoryOrderDetails invOrderDtl : invOrderDetails) {
			Product inventory = inventoryRepository.findByBarcode(invOrderDtl.getBarcode()).get();
			inventory.increaseStockQty(invOrderDtl.getQty()); // increase
			// inventory.setBuyPrice(invOrderDtl.getBuyPrice());// update buy price
			// inventory.setMaxRetailPrice(invOrderDtl.getMaxRetailPrice());
			inventoryRepository.save(inventory);

			transc = new InventoryTransaction(invOrderDtl.getProductName(), invOrderDtl.getBarcode(),
					invOrderDtl.getUom(), AppConstant.STOCK_IN, invOrderDtl.getQty(), dbInvOrder.getPartyName(),
					dbInvOrder.getGstinNumber(), "Stocking in for order : " + dbInvOrder.getOrderId());

			transactions.add(transc);

		}

		// save to inventory Transaction
		productService.addInventoryTransactions(transactions);

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

		if (dbInvOrder.getOutstandingAmount() == payment.getPaidAmount()) {
			dbInvOrder.setPaymentType(AppConstant.PAY_NOW);
			dbInvOrder.setSettled(true);
		} else {
			dbInvOrder.setPaymentType(AppConstant.PAY_LATER);
			dbInvOrder.setSettled(false);
		}

		dbInvOrder.setOutstandingAmount(dbInvOrder.getOutstandingAmount() - payment.getPaidAmount());

		/*
		 * if (payment.getPaymentMode().equals(AppConstant.PAY_NOW)) {
		 * invOrder.setPaymentMode(payment.getPaymentMode()); invOrder.setSettled(true);
		 * } else if (payment.getPaymentMode().equals(AppConstant.PAY_LATER)) {
		 * invOrder.setPaymentMode(payment.getPaymentMode());
		 * 
		 * if (invOrder.getOutstandingAmount() == 0) { invOrder.setSettled(true); } else
		 * { invOrder.setSettled(false); }
		 * 
		 * }
		 */
		// save
		PaymentHistory history = new PaymentHistory(AppConstant.PURCHASE, dbInvOrder.getOrderId().toString(),
				payment.getPaymentMode(), dbInvOrder.getGrandTotal(), payment.getPaidAmount(),
				dbInvOrder.getOutstandingAmount());
		paymentHistoryService.savePaymentDetails(history);

		return inventoryOrderRepository.save(dbInvOrder);

	}

	@Override
	public void deleteInvOrder(String orderId) throws GenericException {
		InventoryOrder invOrder = getInvOrderById(orderId);

		// update stocks
		/*if (invOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED)) {
			List<InventoryOrderDetails> invOrderDetails = inventoryOrderDetailRepository.findByOrderId(orderId);

			if (invOrderDetails != null && !invOrderDetails.isEmpty()) {

				for (InventoryOrderDetails invOrderDtl : invOrderDetails) {
					Product inventory = inventoryRepository.findByBarcode(invOrderDtl.getBarcode()).get();
					inventory.decreaseStockQty(invOrderDtl.getQty()); // increase
					inventoryRepository.save(inventory);
				}
			}
		}

		inventoryOrderRepository.deleteById(orderId);
		// delete order details
		inventoryOrderDetailRepository.deleteAllByOrderId(orderId);
		*/
		
		if (!invOrder.getOrderSts().equals(AppConstant.ORDER_CONFIRMED)) {
			inventoryOrderRepository.deleteById(orderId); // delete order details
			inventoryOrderDetailRepository.deleteAllByOrderId(orderId);
		} else {
			throw new GenericException("Cannot delete as Order has already Confirmed.");
		}

	}
}
