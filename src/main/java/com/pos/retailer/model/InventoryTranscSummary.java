package com.pos.retailer.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryTranscSummary {
	private String barcode;
	private String prdName;
	private String uom;
	
	private double openingStock = -1;
	private double receiveStock = -1;
	private double saleStock = -1;
	private double closeStock = -1;
	private double dumpStock = -1;

	public InventoryTranscSummary(String barcode) {
		this.barcode = barcode;
	}

	public InventoryTranscSummary() {
	}

	public void increaseStockInQty(double qty) {
		if(this.receiveStock == -1)
			this.receiveStock = 0;
		this.receiveStock += qty;
	}
	
	public void increaseStockOutQty(double qty) {
		if(this.saleStock == -1)
			this.saleStock = 0;
		this.saleStock += qty;
	}
	
	public void increaseDumpsQty(double qty) {
		if(this.dumpStock == -1)
			this.dumpStock = 0;
		this.dumpStock += qty;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((barcode == null) ? 0 : barcode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventoryTranscSummary other = (InventoryTranscSummary) obj;
		if (barcode == null) {
			if (other.barcode != null)
				return false;
		} else if (!barcode.equals(other.barcode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
	
}
