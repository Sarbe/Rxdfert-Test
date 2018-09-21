package com.pos.retailer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pos.retailer.model.PaymentHistory;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

	public List<PaymentHistory> findByOrderIdAndOrderType(String orderId,String orderType);

	public List<PaymentHistory> findByOrderIdAndOrderTypeOrderByTransactionDtAsc(String orderId, String orderType);

	@Query("select SUM(p.paidAmount) from PaymentHistory p WHERE p.orderId = ?1 and p.orderType = ?2 ")
	public double getTotalAmountPaid(String orderId,String orderType);
}
