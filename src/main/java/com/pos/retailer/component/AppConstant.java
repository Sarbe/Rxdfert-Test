package com.pos.retailer.component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.pos.retailer.exception.GenericException;

public class AppConstant {
	
	///
	public static boolean invStatus= false;
	
	///
	
	public static final String ORDER_NEW = "NEW";
	public static final String ORDER_SAVED = "SAVED";
	public static final String ORDER_CONFIRMED = "CONFIRMED";
	
	
	
	public static final String PURCHASE = "PURCHASE";
	public static final String SALES = "SALES";
	
	public static final String QTY_ADD = "ADD";
	public static final String QTY_SUB = "SUBTRACT";
	public static final String PAY_NOW = "NOW";
	public static final String PAY_LATER = "LATER";
	
	
	public static final String STOCK_OPEN = "STOCK_OPEN";
	public static final String STOCK_IN = "STOCK_IN";
	public static final String STOCK_OUT = "STOCK_OUT";
	public static final String STOCK_CLOSE = "STOCK_CLOSE";
	public static final String STOCK_DUMP = "STOCK_DUMP";
	
	
	public static final String REPORT_DOWNLOAD = "DOWNLOAD";
	public static final String REPORT_VIEW = "VIEW";
	public static final String CUSTOMER = "CUSTOMER";
	public static final String VENDOR = "VENDOR";

	public static final String PARTY_NAME = "PARTY_NAME";
	public static final String CONTACT_NBR = "CONTACT_NBR";
	public static final String INVOICE_NBR = "INVOICe_NBR";

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_CLERK = "ROLE_CLERK";
	public static final String ROLE_SUPER_USER = "ROLE_SUPER_USER";
	
	public static double roundedValue(double amount) {
		return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
		
	}
	
	public static double roundedValueToNumber(double amount) {
		return BigDecimal.valueOf(amount).setScale(0, RoundingMode.FLOOR).doubleValue();
	}
	
	
	public static void chckInvSts() throws GenericException {
		if (AppConstant.invStatus) {
			throw new GenericException("Could not perform operation as inventory is closed");
		}

	}

}
