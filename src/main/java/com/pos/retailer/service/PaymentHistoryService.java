package com.pos.retailer.service;

import java.util.List;

import com.pos.retailer.model.PaymentHistory;

public interface PaymentHistoryService {

	List<PaymentHistory> getPaymentHistory(String orderId, String orderType);

	void savePaymentDetails(PaymentHistory details);
	
	public double getTotalPayment(String orderId, String orderType);

}