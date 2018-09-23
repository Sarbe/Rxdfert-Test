package com.pos.retailer.utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.impl.upcean.UPCEANLogicImpl;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.stereotype.Component;

import com.google.zxing.WriterException;

@Component
public class EAN13Barcode implements BarCodeManager {
	private static int countryCode = 890;

	public EAN13Barcode() {

	}

	@Override
	public String createBarcode(Long seqNbr) {
		String barcode = countryCode + StringUtils.leftPad(seqNbr.toString(), 9, "0");
		String checkDigit = calculateCodeWithcheckSum(barcode);
		// System.out.println("CS :" + checkDigit);
		barcode += checkDigit;
		return barcode;
	}

	@Override
	public String createBarcodeImage(String barcode) throws IOException, WriterException {

		EAN13Bean bean = new EAN13Bean();

		final int dpi = 150;

		bean.setModuleWidth(0.3);
		bean.setHeight(15);
		// bean.setChecksumMode(ChecksumMode.CP_AUTO);
		bean.setVerticalQuietZone(5);

		// Open output file
		File outputFile = new File("D:/barcode/" + barcode + ".png");
		OutputStream out = new FileOutputStream(outputFile);
		try {
			// Set up the canvas provider for monochrome PNG output
			BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/x-png", dpi,
					BufferedImage.TYPE_BYTE_BINARY, false, 0);

			// Generate the barcode
			bean.generateBarcode(canvas, barcode);

			// Signal end of generation
			canvas.finish();
		} finally {
			out.close();
		}

		return barcode;

	}

	@Override
	public String calculateCodeWithcheckSum(String barcode12) {
		EAN13Bean generator = new EAN13Bean();
		UPCEANLogicImpl impl = generator.createLogicImpl();
		return String.valueOf(impl.calcChecksum(barcode12));
	}
	
	public boolean validate(String barcode) {
		String checkDigit = calculateCodeWithcheckSum(barcode.substring(0, 12));
		if (!checkDigit.equals(barcode.substring(12)))
			return false;
		else
			return true;
	}


}
