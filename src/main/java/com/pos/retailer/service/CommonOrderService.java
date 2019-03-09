package com.pos.retailer.service;

import java.util.List;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.CountBean;
import com.pos.retailer.model.InventoryOrder;
import com.pos.retailer.model.MultiPayment;
import com.pos.retailer.model.SalesOrder;
import com.pos.retailer.report.OutStandingReport.OutstandingSummary;
import com.pos.retailer.repository.model.ProductOverview;

public interface CommonOrderService {

	InventoryOrder getInventoryOrderByOrderId(String orderId) throws GenericException;

	SalesOrder getSalesOrderByOrderId(String orderId) throws GenericException;

	CountBean getCounts();

	void getOrderNbrs();

	List<OutstandingSummary> multiplePayment(MultiPayment paymnet) throws GenericException;

	List<OutstandingSummary> getDetailedOutstandingsForOneParty(String orderType, String partyName);

	List<ProductOverview> getProductOverView(String barcode);

}