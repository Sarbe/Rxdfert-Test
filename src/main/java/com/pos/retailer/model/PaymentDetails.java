package com.pos.retailer.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails {
	private double paidAmount;
	private String paymentMode; // Cash/Credit/Debit/Net Banking/Mobile App
	private String paymentType; // Credit sale/cash sale
	private double instantDiscount;

	public void setPaidAmount(double paidAmount) {
		BigDecimal bd = new BigDecimal(paidAmount).setScale(2, RoundingMode.FLOOR);
        this.paidAmount = bd.doubleValue();
	}


	public void setInstantDiscount(double instantDiscount) {
		BigDecimal bd = new BigDecimal(instantDiscount).setScale(2, RoundingMode.FLOOR);
        this.instantDiscount = bd.doubleValue();
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}


	public PaymentDetails(double paidAmount, String paymentType) {
		super();
		this.paidAmount = paidAmount;
		this.paymentType = paymentType;
	}


	
}
