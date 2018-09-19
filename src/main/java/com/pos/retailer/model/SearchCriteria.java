package com.pos.retailer.model;

import org.apache.commons.lang.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria {

	private String orderType;
	private String searchType;
	private String searchValue;

	public boolean isEmpty() {
		boolean ret = false;
		if (StringUtils.trimToEmpty(this.orderType).equals("") || StringUtils.trimToEmpty(this.searchType).equals("")
				|| StringUtils.trimToEmpty(this.searchValue).equals("")) {
			ret = true;
		}
		return ret;
	}
}
