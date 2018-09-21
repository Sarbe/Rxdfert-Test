package com.pos.retailer.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pos.retailer.component.AppConstant;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory_order")
@Getter
@Setter
public class InventoryOrder extends Auditable implements Serializable {

	private static final long serialVersionUID = 3902874255810197321L;
	@Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private String orderId;

	@Column(name = "bill_nbr", unique = true)
	private String billNbr; // receipt number

	@Size(max = 50)
	@Column(name = "party_name")
	private String partyName;

	@Size(max = 15)
	@Column(name = "contact_nbr")
	private String contactNbr;

	@Size(max = 30)
	@Column(name = "gstin")
	private String gstinNumber;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "activity_dt")
	private LocalDateTime activityDt = LocalDateTime.now();

	@Column(name = "order_sts")
	private String orderSts;

	@Column(name = "sub_total")
	private double subTotal = 0;

	@Column(name = "grand_total")
	private double grandTotal = 0;

	@Column(name = "tax")
	private double tax = 0;

	@Column(name = "outstanding")
	private double outstandingAmount;

	@Column(name = "settled")
	private boolean settled;// = false;

	// @JsonIgnore
	@Column(name = "payment_type")
	private String paymentType = AppConstant.PAY_LATER; // NOW/LATER

	/*
	 * @JsonManagedReference
	 * 
	 * @OneToMany(mappedBy = "inventoryOrder", cascade = CascadeType.ALL) private
	 * List<InventoryOrderDetailsDto> orderDetails = new ArrayList<>();
	 */

	public InventoryOrder() {
	}

	public InventoryOrder(String orderId) {
		this.orderId = orderId;

	}
	
	public void calculateOrderAmount(List<InventoryOrderDetails> invOrderDetails){
		double orderTotal = 0;
		for (InventoryOrderDetails invOrderDtl : invOrderDetails) {
			orderTotal += invOrderDtl.getTotalAmount();
		}
		
		setGrandTotal(orderTotal);
		setOutstandingAmount(orderTotal);
	}

	/// Setter override

	public void setSubTotal(double subTotal) {
		this.subTotal = AppConstant.roundedValue(subTotal);
	}

	public void setGrandTotal(double grandTotal) {
		this.grandTotal = AppConstant.roundedValue(grandTotal);
	}

	public void setTax(double tax) {
		this.tax = AppConstant.roundedValue(tax);
	}

	public void setOutstandingAmount(double outstandingAmount) {
		this.outstandingAmount = AppConstant.roundedValue(outstandingAmount);
	}
	////////

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
