package com.pos.retailer.report.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InventoryActivities {
	
	private String productName;

	private String barcode;

	private String unit;

	private float openingStock;

	private float closingStock;

	private float receivedStock;

	private float consumedStock;

	private float transferOutStock;

}
