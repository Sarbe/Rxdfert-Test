package com.pos.retailer.service.Impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.retailer.model.PaymentHistory;
import com.pos.retailer.repository.PaymentHistoryRepository;
import com.pos.retailer.service.PaymentHistoryService;

@Service
@Transactional
public class PaymentHistoryServiceImpl implements PaymentHistoryService {

	@Autowired
	private PaymentHistoryRepository paymentHistoryRepository;

	@Override
	public List<PaymentHistory> getPaymentHistory(String orderId, String orderType) {
		return paymentHistoryRepository.findByOrderIdAndOrderTypeOrderByTransactionDtAsc(orderId, orderType);
	}
	
	@Override
	public void savePaymentDetails(PaymentHistory details) {
		paymentHistoryRepository.save(details);

	}
	
}
