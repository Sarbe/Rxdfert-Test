package com.pos.retailer.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompositePK implements Serializable {
	private static final long serialVersionUID = 1L;
	private String contactNbr;
	private String customerType;

	public CompositePK() {
		// TODO Auto-generated constructor stub
	}

	public CompositePK(String contactNbr, String customerType) {

		this.contactNbr = contactNbr;
		this.customerType = customerType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contactNbr == null) ? 0 : contactNbr.hashCode());
		result = prime * result + ((customerType == null) ? 0 : customerType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompositePK other = (CompositePK) obj;
		if (contactNbr == null) {
			if (other.contactNbr != null)
				return false;
		} else if (!contactNbr.equals(other.contactNbr))
			return false;
		if (customerType == null) {
			if (other.customerType != null)
				return false;
		} else if (!customerType.equals(other.customerType))
			return false;
		return true;
	}



}