package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pos.retailer.model.InventoryOrderDetails;

public interface OrderDetailRepository extends JpaRepository<InventoryOrderDetails, Long> {

	public Optional<InventoryOrderDetails> findByOrderDetailId(Long id);

	public List<InventoryOrderDetails> findByOrderId(String orderId);
	
	public List<InventoryOrderDetails> findByOrderIdAndBarcode(Long orderId, String barcode);

	public void deleteAllByOrderId(Long orderId);

}
