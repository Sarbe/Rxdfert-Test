package com.pos.retailer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "customer")
@Getter
@Setter
@IdClass(CompositePK.class)
public class CustomerDetails extends Auditable {

	private static final long serialVersionUID = -3804936607275220286L;

	@Id
	@Size(max = 15)
	@Column(name = "contact_nbr")
	private String contactNbr;

	@Id
	@Column(name = "cust_type")
	private String customerType;

	@Size(max = 50)
	@Column(name = "party_name")
	// @EmbeddedId
	private String partyName;

	@Size(max = 100)
	@Column(name = "address", nullable = true)
	private String address;

	@Size(max = 30)
	@Column(name = "gstin", nullable = true)
	private String gstinNumber;

	@Transient
	private String actions = "";
	
	public CustomerDetails(String customerType, @Size(max = 50) String partyName, @Size(max = 15) String contactNbr,
			@Size(max = 30) String gstinNumber, @Size(max = 100) String address) {
		super();
		this.customerType = customerType;
		this.partyName = StringUtils.trimToEmpty(partyName).toUpperCase();
		this.contactNbr = contactNbr;
		this.address = address;
		this.gstinNumber = gstinNumber;
		
		
	}

	public boolean checkEmpty() {
		if (StringUtils.isEmpty(this.contactNbr) && StringUtils.isEmpty(this.partyName)) {
			return true;
		}
		return false;
	}

	public CustomerDetails() {
		super();
	}

	public CustomerDetails(@Size(max = 15) String contactNbr, String customerType) {
		super();
		this.contactNbr = contactNbr;
		this.customerType = customerType;
	}

}