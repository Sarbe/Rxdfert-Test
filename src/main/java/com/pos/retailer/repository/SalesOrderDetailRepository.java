package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pos.retailer.model.SalesOrderDetails;

public interface SalesOrderDetailRepository extends JpaRepository<SalesOrderDetails, Long> {

	public Optional<SalesOrderDetails> findByOrderDetailId(Long id);

	public List<SalesOrderDetails> findByOrderId(String orderId);

	public SalesOrderDetails findByOrderIdAndBarcode(String orderId, String barcode);

	public void deleteAllByOrderId(String orderId);

}
