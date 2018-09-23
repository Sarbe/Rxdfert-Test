package com.pos.retailer.utility;

import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;
import org.springframework.stereotype.Component;

@Component
public class EAN8Barcode {

	public EAN8Barcode() {

	}

	public String calculateCodeWithcheckSum(String barcode7) {
		EAN8Bean generator = new EAN8Bean();
		UPCEANLogicImpl impl = generator.createLogicImpl();
		return String.valueOf(impl.calcChecksum(barcode7));
	}

	public boolean validate(String barcode) {
		String checkDigit = calculateCodeWithcheckSum(barcode.substring(0, 7));
		if (!checkDigit.equals(barcode.substring(7)))
			return false;
		else
			return true;
	}

}
