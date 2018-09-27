package com.pos.retailer.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.pos.retailer.component.AppConstant;

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
        this.paidAmount = AppConstant.roundedValue(paidAmount);
	}


	public void setInstantDiscount(double instantDiscount) {
        this.instantDiscount = AppConstant.roundedValue(instantDiscount);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}


	public PaymentDetails(double paidAmount, String paymentMode) {
		super();
		setPaidAmount(paidAmount);
		this.paymentMode = paymentMode;
	}


	
}
