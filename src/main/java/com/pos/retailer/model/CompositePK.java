package com.pos.retailer.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompositePK implements Serializable {
	private static final long serialVersionUID = 1L;
	private String partyName;
	private String customerType;

	public CompositePK() {
		// TODO Auto-generated constructor stub
	}

	public CompositePK(String partyName, String customerType) {

		this.partyName = partyName;
		this.customerType = customerType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customerType == null) ? 0 : customerType.hashCode());
		result = prime * result + ((partyName == null) ? 0 : partyName.hashCode());
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
		if (customerType == null) {
			if (other.customerType != null)
				return false;
		} else if (!customerType.equals(other.customerType))
			return false;
		if (partyName == null) {
			if (other.partyName != null)
				return false;
		} else if (!partyName.equals(other.partyName))
			return false;
		return true;
	}

}