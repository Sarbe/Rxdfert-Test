package com.pos.retailer.repository.model;

public interface CustomOutstanding {
	String getContactNbr();

	String getPartyName();

	String getOrderId();

	double getTotalAmount();
	
	double getDiscount();

	double getOutstanding();

}
