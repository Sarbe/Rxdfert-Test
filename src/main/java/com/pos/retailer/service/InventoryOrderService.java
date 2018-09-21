package com.pos.retailer.service;

import java.util.List;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.CustomerDetails;
import com.pos.retailer.model.InventoryOrder;
import com.pos.retailer.model.InventoryOrderDto;
import com.pos.retailer.model.PaymentDetails;

public interface InventoryOrderService {

	InventoryOrder getInvOrderById(String id) throws GenericException;

	InventoryOrder saveInvOrder(InventoryOrder order) throws GenericException;

	List<InventoryOrder> getAllInvOrders();

	void deleteInvOrder(String orderId) throws GenericException;

	String getInvOrderStatus(String id) throws GenericException;

	InventoryOrder submitOrder(String orderId, InventoryOrder invOrder) throws GenericException;

	List<CustomerDetails> getUniqueVendorDetails();

	InventoryOrder updatePaymentDetails(String orderId, PaymentDetails payment) throws GenericException;

	InventoryOrder generateOrderId();

	InventoryOrderDto getFullInvOrderById(String invOrderid) throws GenericException;

	List<InventoryOrder> getAllInvOrdersByStatus(List<String> status);

	CustomerDetails getCustomerDetailsByContactNbr(String contactNbr);

	InventoryOrder receivePaymentDetails(String orderId, PaymentDetails payment) throws GenericException;

	InventoryOrderDto recal(String orderId);

}