package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pos.retailer.model.InventoryOrderDetails;

public interface InventoryOrderDetailRepository extends JpaRepository<InventoryOrderDetails, Long> {

	public Optional<InventoryOrderDetails> findByOrderDetailId(Long orderDtlid);

	public List<InventoryOrderDetails> findByOrderId(String orderId);

	public InventoryOrderDetails findByOrderIdAndBarcode(String orderId, String barcode);
	
	public InventoryOrderDetails findByOrderDetailIdAndOrderId(Long orderDtlId, String orderId);

	public void deleteAllByOrderId(String orderId);

}
