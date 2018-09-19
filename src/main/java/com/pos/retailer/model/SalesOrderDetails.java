package com.pos.retailer.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.pos.retailer.component.AppConstant;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sales_order_details")
@Getter
@Setter
public class SalesOrderDetails extends Auditable implements Serializable {

	private static final long serialVersionUID = 3902874255810197321L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_detail_id")
	private Long orderDetailId;

	@Column(nullable = false)
	private String barcode;

	@Size(max = 50)
	@Column(name = "product_name")
	private String productName;

	@Column(nullable = true)
	private double tax = 0;

	@Column(name = "mrp")
	private double mrp = 0;

	@Column(name = "sell_price")
	private double sellPrice = 0; // without Tax

	@Column(name = "sub_total")
	private double subTotal = 0; // without Tax

	@Column(name = "total_Amount")
	private double totalAmount = 0; // with Tax

	@NotNull
	private int qty;

	@NotNull
	private String uom;

	// @JsonIgnore
	/*
	 * @JsonBackReference
	 * 
	 * @ManyToOne(fetch = FetchType.EAGER)
	 * 
	 * @JoinColumn(name = "order_id") private InventoryOrderDto inventoryOrder = new
	 * InventoryOrderDto();
	 */

	@Column(name = "order_id", nullable = false)
	private String orderId;

	public SalesOrderDetails() {
	}

	public SalesOrderDetails(String barcode, Product product, int qty, String orderId) {
		super();
		this.barcode = barcode;
		this.productName = product.getProductName();
		this.sellPrice = product.getSellPrice();
		this.tax = product.getTax();
		this.qty = qty;
		this.uom = product.getUom();
		this.orderId = orderId;

	}

	public void calculateAmount(Product product) {
		// this.totalAmount = AppConstant.roundedValueToNumber(this.qty *
		// this.sellPrice);
		// this.subTotal = AppConstant.roundedValue((this.totalAmount / (100 +
		// product.getTax())) * 100);
		// this.tax = AppConstant.roundedValue(this.totalAmount - this.subTotal);
		// this.mrp = AppConstant.roundedValue(product.getMaxRetailPrice());

		setTotalAmount(this.qty * this.sellPrice);
		setSubTotal((this.totalAmount / (100 + product.getTax())) * 100);
		setTax(this.totalAmount - this.subTotal);
		setMrp(product.getMaxRetailPrice());
	}

	public void increaseQty(int qty) {
		this.qty += qty;
	}

	public void decreaseQty(int qty) {
		this.qty -= qty;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	//////////// override setter
	public void setTax(double tax) {
		this.tax = AppConstant.roundedValue(tax);
	}

	public void setMrp(double mrp) {
		this.mrp = AppConstant.roundedValue(mrp);
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = AppConstant.roundedValue(sellPrice);
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = AppConstant.roundedValue(subTotal);
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = AppConstant.roundedValueToNumber(totalAmount);
	}

}