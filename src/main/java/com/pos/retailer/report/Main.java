package com.pos.retailer.report;

public class Main {

	public static void main(String[] args) throws Exception {
		ReportUtility report = new BarCodeReport();
		report.prepareReport("48","400580816951","SAMSUNG LED TV BIG");
		report.generateReport("bar");
		
		System.out.println("done");
		
		

	}

}
