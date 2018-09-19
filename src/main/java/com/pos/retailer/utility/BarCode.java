package com.pos.retailer.utility;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import com.google.zxing.WriterException;

public class BarCode {


	public BarCode() {

	}

	public static void main(String[] args) throws IOException, WriterException {

		String nbr = "111-23456";

		Code128Bean bean = new Code128Bean();

		// UPCEANLogicImpl impl = bean.createLogicImpl();
		// char c = UPCEANLogicImpl.calcChecksum(nbr);
		// System.out.println(c);

		final int dpi = 150;

		bean.setModuleWidth(0.3);
		bean.setHeight(15);
		//bean.setChecksumMode(ChecksumMode.CP_AUTO);
		//bean.setVerticalQuietZone(0);
		// bean.doQuietZone(false);

		// Open output file
		File outputFile = new File("D:/out.png");
		OutputStream out = new FileOutputStream(outputFile);
		try {
			// Set up the canvas provider for monochrome PNG output
			BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/x-png", dpi,
					BufferedImage.TYPE_BYTE_BINARY, false, 0);

			// Generate the barcode
			bean.generateBarcode(canvas, nbr);

			// Signal end of generation
			canvas.finish();
		} finally {
			out.close();
		}

	}

}
