package com.pos.retailer.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "payment_transaction")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PaymentHistory extends Auditable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String orderType; // PURCHASE/SALES
	private String orderId;

	@Column(name = "payment_mode")
	private String paymentMode; // = CASH/CARD/APP

	@Column(name = "payment_type")
	private String paymentType;// Now/Later

	@Column(name = "total_amount")
	private double totalAmount;

	@Column(name = "paid_amount")
	private double paidAmount;

	@Column(name = "outstanding_amount")
	private double outstandingAmount;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "transaction_dt", nullable = false)
	private LocalDateTime transactionDt = LocalDateTime.now();

	public PaymentHistory(String orderType, String orderId, String paymentType, String paymentMode, double totalAmount,
			double paidAmount, double outstandingAmount) {
		super();
		this.orderType = orderType;
		this.orderId = orderId;
		setPaidAmount(paidAmount);
		setOutstandingAmount(outstandingAmount);
		this.paymentMode = paymentMode;
		this.paymentType = paymentType;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	//// Setter override
	public void setTotalAmount(double totalAmount) {
		BigDecimal bd = new BigDecimal(totalAmount).setScale(2, RoundingMode.FLOOR);
		this.totalAmount = bd.doubleValue();
	}

	public void setPaidAmount(double paidAmount) {
		BigDecimal bd = new BigDecimal(paidAmount).setScale(2, RoundingMode.FLOOR);
		this.paidAmount = bd.doubleValue();
	}

	public void setOutstandingAmount(double outstandingAmount) {
		BigDecimal bd = new BigDecimal(outstandingAmount).setScale(2, RoundingMode.FLOOR);
		this.outstandingAmount = bd.doubleValue();
	}

}
