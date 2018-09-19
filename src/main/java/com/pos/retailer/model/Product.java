package com.pos.retailer.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.pos.retailer.component.AppConstant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product extends Auditable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long productId;

	// @Id
	@Column(nullable = false, unique = true)
	@Size(max = 15)
	private String barcode;

	/*
	 * @Size(max = 50)
	 * 
	 * @Column(name = "category") private String category;
	 */

	@Size(max = 50)
	@Column(name = "product_name", nullable = false, unique = true)
	private String productName;

	@Size(max = 50)
	@Column(name = "hsn", nullable = true)
	private String hsn;

	@Size(max = 50)
	@Column(name = "manufacturer", nullable = true)
	private String manufacturer;

	@Column(name = "max_price", nullable = true)
	private double maxRetailPrice;

	@Column(name = "sell_price", nullable = true)
	private double sellPrice;

	@Column(name = "buy_price", nullable = true)
	private double buyPrice;

	@Column(name = "tax", nullable = true)
	private double tax;

	@Column(name = "threshold", nullable = true)
	private double thresholdQty = 0;

	@Column(nullable = true)
	private double discount = 0;

	@Column(nullable = true)
	private double stockQty;

	@Column(nullable = true)
	private boolean availability;

	@Column(nullable = true)
	private LocalDate expiryDt;

	@Column(nullable = false)
	private String uom;

	// User Defined Function

	public void increaseStockQty(int qty) {
		this.stockQty += qty;
	}

	public void decreaseStockQty(int qty) {
		this.stockQty -= qty;
	}

	public void setDiscount(double discount) {
		this.discount = AppConstant.roundedValue(discount);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	// override setter

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = AppConstant.roundedValue(buyPrice);
	}

	public double getDiscount() {
		return discount;
	}

	public void setMaxRetailPrice(double maxRetailPrice) {
		this.maxRetailPrice = AppConstant.roundedValue(maxRetailPrice);
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = AppConstant.roundedValue(sellPrice);
	}

	public void setTax(double tax) {
		this.tax = AppConstant.roundedValue(tax);
	}

	public void setThresholdQty(double thresholdQty) {
		this.thresholdQty = AppConstant.roundedValueToNumber(thresholdQty);
	}

	public void setStockQty(double stockQty) {
		this.stockQty = AppConstant.roundedValueToNumber(stockQty);
	}

}
