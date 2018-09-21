package com.pos.retailer.service;

import java.util.List;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.InventoryOrderDetails;

public interface InventoryOrderDetailsService {

	InventoryOrderDetails getOrderDetailById(Long orderDtlId) throws GenericException;

	InventoryOrderDetails addOrderDetail(String orderId, InventoryOrderDetails orderDetail) throws GenericException;

	void deleteOrderDetailById(String orderDtlid, Long orderDetailId) throws GenericException;

	List<InventoryOrderDetails> getOrderDetailsByOrderId(String orderId);

	InventoryOrderDetails changeQtyByOne(String orderId, Long orderDtlId, String changeType) throws GenericException;

	InventoryOrderDetails changeQty(String orderId, Long orderDtlId, int qty) throws GenericException;

}