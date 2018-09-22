package com.pos.retailer.model;

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
@Table(name = "inventory_order_details")
@Getter
@Setter
public class InventoryOrderDetails extends Auditable {

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

	@Column(name = "buy_price", nullable = false)
	private double buyPrice;
	
	@Column(name = "max_price", nullable = false)
	private double maxRetailPrice;

	@Column(nullable = false)
	private double totalAmount;

	@Column(nullable = true)
	private double tax = 0;

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

	public InventoryOrderDetails() {
	}

	
	/// Override setter
	
	public void setBuyPrice(double buyPrice) {
        this.buyPrice =   AppConstant.roundedValue(buyPrice);
	}


	public void setMaxRetailPrice(double maxRetailPrice) {
        this.maxRetailPrice =   AppConstant.roundedValue(maxRetailPrice);
	}


	public void setTotalAmount(double totalAmount) {
        this.totalAmount =   AppConstant.roundedValue(totalAmount);
	}


	public void setTax(double tax) {
        this.tax =   AppConstant.roundedValue(tax);
	}

	//
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	

	public void increaseQty(int qty) {
		this.qty += qty;
	}

	public void decreaseQty(int qty) {
		this.qty -= qty;
	}

	public void calculateAmount() {
        setTotalAmount(this.qty * this.buyPrice);

	}
}