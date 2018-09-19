package com.pos.retailer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vendors")
@Getter
@Setter
public class VendorDetails {

	@Id
	@Size(max = 15)
	@Column(name = "contact_nbr")
	private String contactNbr;

	@Size(max = 50)
	@Column(name = "party_name")
	private String partyName;

	@Size(max = 30)
	@Column(name = "gstin", nullable = true)
	private String gstinNumber;

	public boolean isEmpty() {
		if (StringUtils.isEmpty(this.contactNbr) && StringUtils.isEmpty(this.partyName)) {
			return true;
		}
		return false;
	}

	public VendorDetails(@Size(max = 15) String contactNbr, @Size(max = 50) String partyName,
			@Size(max = 30) String gstinNumber) {
		super();
		this.contactNbr = contactNbr;
		this.partyName = partyName;
		this.gstinNumber = gstinNumber;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
