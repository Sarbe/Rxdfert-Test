package com.pos.retailer.service;

import java.util.List;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.SalesOrderDetails;

public interface SalesOrderDetailsService {

	SalesOrderDetails getOrderDetailById(Long orderDtlId) throws GenericException;

	List<SalesOrderDetails> getOrderDetailsByOrderId(String orderId);

	void deleteOrderDetailById(String orderId, Long orderDtlid) throws GenericException;

	SalesOrderDetails changeQtyByOne(String orderId, Long orderDtlId, String changeType) throws GenericException;

	SalesOrderDetails saveOrderDetail(String orderId, String barcode) throws GenericException;

	//SalesOrderDetails changeQuantity(String orderId, Long orderDtlId, int quantity) throws GenericException;

	SalesOrderDetails saveProductByBarcodeAndQty(String orderId, String barcode, int qty) throws GenericException;

	SalesOrderDetails changeQty(String orderId, Long orderDtlId, int qty) throws GenericException;

}