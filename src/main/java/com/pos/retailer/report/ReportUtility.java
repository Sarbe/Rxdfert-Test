package com.pos.retailer.report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

@Component
public abstract class ReportUtility {

	protected ApplicationContext context;

	public ReportUtility() {
		// TODO Auto-generated constructor stub
	}
	
	public ReportUtility(ApplicationContext context) {
		this.context = context;
	}

	Logger logger = LoggerFactory.getLogger(ReportUtility.class);

	protected JasperPrint jasperPrint;
	protected JasperReport jasperReport;
	protected Map<String, Object> reportParam = new HashMap<String, Object>();

	protected JRBeanCollectionDataSource listSDataSource;
	protected JRMapCollectionDataSource mapDataSource;
	protected String fileName;

	public abstract void reportConstructor(Object... inputData) throws Exception;

	public abstract void reportMetadata(String reportParam) throws JRException, IOException;

	public abstract void fillReport() throws JRException, FileNotFoundException, IOException;

	public void prepareReport(String reportParam, Object... inputData) throws Exception {
		reportConstructor(inputData);
		reportMetadata(reportParam);
		fillReport();
	}

	// TODO Auto-generated method stub
	public void generateReport(String reportName) throws JRException {

	}

	public byte[] generateReportByte(String orderId) throws JRException {
		// TODO Auto-generated method stub
		return null;
	}

}
