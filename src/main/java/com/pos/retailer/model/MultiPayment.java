package com.pos.retailer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MultiPayment {
	private double amount;
	private String paymentMode;
	private String partyName;
	private String orderType;
}
