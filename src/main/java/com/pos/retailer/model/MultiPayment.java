package com.pos.retailer.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class MultiPayment {
	SearchCriteria searchCriteria = new SearchCriteria();
	private double amount;
	private String paymentMode;
	private String contactNbr;
	private String custType;

}
