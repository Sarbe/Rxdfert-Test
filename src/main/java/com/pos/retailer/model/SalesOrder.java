package com.pos.retailer.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pos.retailer.component.AppConstant;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sales_order")
@Getter
@Setter
public class SalesOrder implements Serializable {

	private static final long serialVersionUID = 3902874255810197321L;

	@Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private String orderId;

	@Column(name = "receipt_nbr", unique = true)
	private String receiptNumber; // receipt number

	@Size(max = 50)
	@Column(name = "party_name")
	private String partyName;

	@Size(max = 15)
	@Column(name = "contact_nbr")
	private String contactNbr;

	@Size(max = 100)
	@Column(name = "address")
	private String address;

	@Size(max = 30)
	@Column(name = "gstin")
	private String gstinNumber;

	@Column
	private String activity;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "activity_dt")
	private LocalDateTime activityDt = LocalDateTime.now();

	@Column(name = "order_sts")
	private String orderSts; // new/save/confirm

	@Column(name = "total_mrp")
	private double totalMrp = 0;

	@Column(name = "sub_total")
	private double subTotal = 0; // without Tax

	@Column(name = "grand_total")
	private double grandTotal = 0; // With Tax

	@Column(name = "discount")
	private double discount = 0;

	@Column(name = "tax_amount")
	private double taxAmount = 0;

	@Column(name = "outstanding")
	private double outstandingAmount;

	@Column(name = "settled")
	private boolean settled;// = false;

	@Column(name = "payment_type")
	private String paymentType = StringUtils.EMPTY;// = AppConstant.PAY_LATER; // NOW/LATER


	/*
	 * @JsonManagedReference
	 * 
	 * @OneToMany(mappedBy = "inventoryOrder", cascade = CascadeType.ALL) private
	 * List<InventoryOrderDetailsDto> orderDetails = new ArrayList<>();
	 */

	public SalesOrder() {

	}

	public SalesOrder(String orderId) {
		this.orderId = orderId;
	}

	/////////////// Override Setter

	public void setGrandTotal(double grandTotal) {
		this.grandTotal = AppConstant.roundedValue(grandTotal);
	}

	public void setDiscount(double discount) {
		this.discount = AppConstant.roundedValue(discount);
	}

	public void setTotalMrp(double totalMrp) {
		this.totalMrp = AppConstant.roundedValue(totalMrp);
	}

	public void setSubTotal(double subTotal) {
		this.subTotal = AppConstant.roundedValue(subTotal);
	}

	public void setTaxAmount(double taxAmount) {
		this.taxAmount = AppConstant.roundedValue(taxAmount);
	}

	public void setOutstandingAmount(double outstandingAmount) {
		this.outstandingAmount = AppConstant.roundedValue(outstandingAmount);
	}

	///////////////////////////////

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
