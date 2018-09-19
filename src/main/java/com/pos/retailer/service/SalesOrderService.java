package com.pos.retailer.service;

import java.util.List;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.CustomerDetails;
import com.pos.retailer.model.PaymentDetails;
import com.pos.retailer.model.SalesOrder;
import com.pos.retailer.model.SalesOrderDto;

public interface SalesOrderService {

	List<SalesOrder> getAllSalesOrders();

	List<CustomerDetails> getUniqueCustomerDetails();

	SalesOrder saveSalesOrder(SalesOrder salesOrder) throws GenericException;

	SalesOrder getSalesOrderById(String id) throws GenericException;

	String getSalesOrderStatus(String id) throws GenericException;

	void deleteSalesOrder(String orderId) throws GenericException;

	SalesOrder confirmSalesOrder(String orderId, SalesOrder salesOrder) throws GenericException;

	SalesOrder updatePaymentDetails(String orderId, PaymentDetails payment) throws GenericException;

	Object getCustomerDetailsByContactNbr(String contactNbr);

	SalesOrderDto getFullSalesDetails(String id) throws GenericException;

	SalesOrder generateOrderId();

	//List<SalesOrder> getAllSalesOrdersByStatus(String status);

	SalesOrderDto receivePayment(String orderId, PaymentDetails payment) throws GenericException;

	List<SalesOrder> getAllSalesOrdersByStatus(List<String> status);
}