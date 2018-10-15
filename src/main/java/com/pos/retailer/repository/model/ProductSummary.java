package com.pos.retailer.repository.model;

import com.pos.retailer.component.AppConstant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSummary {
	private long productCount;
	private double stockValue;

	public ProductSummary(long productCount, double stockValue) {
		super();
		this.productCount = productCount;
		setStockValue(stockValue);
	}

	public void setStockValue(double stockValue) {
		this.stockValue = AppConstant.roundedValue(stockValue);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	static class ProductSaleSummary {
		private String barcode;
		private String productName;
		private double stockInQty;
		private double stockOutQty;
		public ProductSaleSummary(String barcode, String productName, double stockInQty, double stockOutQty) {
			super();
			this.barcode = barcode;
			this.productName = productName;
			this.stockInQty = stockInQty;
			this.stockOutQty = stockOutQty;
		}
		
		
	}
}
