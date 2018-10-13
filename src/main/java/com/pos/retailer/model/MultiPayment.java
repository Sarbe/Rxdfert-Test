package com.pos.retailer.model;

import com.pos.retailer.component.AppConstant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MultiPayment {
	private double amount;
	private String paymentMode;
	private String partyName;
	private String orderType;
	
	public void setAmount(double amount) {
		this.amount = AppConstant.roundedValue(amount);
	}
	
	
}
