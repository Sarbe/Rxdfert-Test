package com.pos.retailer.utility;

import java.io.IOException;

import com.google.zxing.WriterException;

public interface BarCodeManager {

	String createBarcode(Long seqNbr);

	String createBarcodeImage(String barcode) throws IOException, WriterException;

	String calculateCodeWithcheckSum(String barcode12);

	

}
