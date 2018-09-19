package com.pos.retailer.service;

import java.util.List;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.CountBean;
import com.pos.retailer.model.InventoryOrder;
import com.pos.retailer.model.MultiPayment;
import com.pos.retailer.model.SalesOrder;
import com.pos.retailer.model.SearchCriteria;
import com.pos.retailer.repository.model.CustomOutstanding;

public interface CommonOrderService {

	InventoryOrder getInventoryOrderByOrderId(String orderId) throws GenericException;

	SalesOrder getSalesOrderByOrderId(String orderId) throws GenericException;

	CountBean getCounts();

	void getOrderNbrs();
	
	List<CustomOutstanding> getSummaryBySearchCriteriaGroupedBy(SearchCriteria search) throws GenericException;
	
	List<CustomOutstanding> multiplePayment(MultiPayment paymnet) throws GenericException;

//	List<CustomPartyDetails> getGroupedPartyDetails(String custType);


	
}