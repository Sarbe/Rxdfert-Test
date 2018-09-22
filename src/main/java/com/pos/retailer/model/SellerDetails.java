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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seller")
@Getter
@Setter
@NoArgsConstructor
public class SellerDetails extends Auditable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(max = 30)
	private String shopName;

	@NotNull
	@Size(max = 30)
	private String ownership;

	@NotNull
	@Size(max = 30)
	private String gstInNumber;

	@NotNull
	@Size(max = 30)
	private String addressLine1;

	@Column(nullable = true)
	@Size(max = 30)
	private String addressLine2;

	@Column(nullable = true)
	@Size(max = 30)
	private String addressLine3;

	@NotNull
	@Size(max = 20)
	private String city;

	@NotNull
	@Size(max = 10)
	private String pincode;

	@Column(nullable = true)
	@Size(max = 20)
	private String businessPhone1;
	
	@Column(nullable = true)
	@Size(max = 20)
	private String businessPhone2;

	@Column(nullable = true)
	@Size(max = 40)
	private String feedbackEmail;

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
