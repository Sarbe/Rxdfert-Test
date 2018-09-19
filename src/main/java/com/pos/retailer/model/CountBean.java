package com.pos.retailer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CountBean {

	private int purchaseAll;
	private int purchaseNew;
	private int purchaseSettled;
	private int purchaseUnSettled;
	
	
	private int salesAll;
	private int salesNew;
	private int salesSettled;
	private int salesUnSettled;
	
	private int productAll;
	private int productAvail;
	private int productNotAvail;
	private int stockLess;
	
}
