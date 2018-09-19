package com.pos.retailer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pos.retailer.model.PaymentHistory;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

	public List<PaymentHistory> findByOrderIdAndOrderType(String orderId,String orderType);

	public List<PaymentHistory> findByOrderIdAndOrderTypeOrderByTransactionDtAsc(String orderId, String orderType);

}
