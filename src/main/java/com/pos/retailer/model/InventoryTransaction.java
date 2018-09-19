package com.pos.retailer.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
public class InventoryTransaction extends Auditable implements Serializable {

	private static final long serialVersionUID = 7195801748349100453L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "product_name", nullable = false)
	private String productName;

	@NotNull
	private String barcode;

	@NotNull
	private String uom;
	
	@NotNull
	private String activity;

	@NotNull
	private double qty;

	@Size(max = 50)
	@Column(name = "party_name", nullable = true)
	private String partyName;

	@Size(max = 30)
	@Column(name = "gstin", nullable = true)
	private String gstinNumber;

	@Column(nullable = true)
	@Size(max = 100)
	private String reason;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "activity_dt", nullable = false)
	private LocalDateTime activityDt = LocalDateTime.now();

	@Transient
	private int count;

	public InventoryTransaction() {
		super();
	}

	public InventoryTransaction(@NotNull String productName, @NotNull String barcode, @NotNull String uom, @NotNull String activity,
			@NotNull double qty, @Size(max = 50) String partyName, @Size(max = 30) String gstinNumber,
			@Size(max = 100) String reason) {
		super();
		this.productName = productName;
		this.barcode = barcode;
		this.uom = uom;
		this.activity = activity;
		this.qty = qty;
		this.partyName = partyName;
		this.gstinNumber = gstinNumber;
		this.reason = reason;
	}

	public InventoryTransaction(@NotNull String productName, @NotNull String barcode, @NotNull String uom, @NotNull String activity,
			@NotNull double qty, @Size(max = 100) String reason) {
		super();
		this.productName = productName;
		this.barcode = barcode;
		this.activity = activity;
		this.uom = uom;
		this.qty = qty;
		this.reason = reason;
	}

	public InventoryTransaction(@NotNull String barcode) {
		super();
		this.barcode = barcode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((barcode == null) ? 0 : barcode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventoryTransaction other = (InventoryTransaction) obj;
		if (barcode == null) {
			if (other.barcode != null)
				return false;
		} else if (!barcode.equals(other.barcode))
			return false;
		return true;
	}

}
