package com.pos.retailer.report;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Component
public class BarCodeReport extends ReportUtility {

	@Autowired
	ResourceLoader rl;

	@Override
	public void reportConstructor(Object... inputData) throws Exception {
		// listSDataSource = new JREmptyDataSource();
		reportParam.put("BARCODE", (String) inputData[0]);
		reportParam.put("prdName", (String) inputData[1]);
	}

	@Override
	public void reportMetadata(String reportParam) throws JRException, IOException {
		System.out.println("BarCodeReport.fillReport()");

		// FileInputStream is = new FileInputStream(file);

		String reportNm = "Barcode";
		if (StringUtils.isNotEmpty(reportParam)) {
			reportNm += reportParam; // report Page size
		} else {
			reportNm += "48";
		}

		InputStream resource = new ClassPathResource(reportNm + ".jasper").getInputStream();
		jasperReport = (JasperReport) JRLoader.loadObject(resource);

	}

	@Override
	public void fillReport() throws JRException, IOException {

		jasperPrint = JasperFillManager.fillReport(jasperReport, reportParam, new JREmptyDataSource());

	}

	@Override
	public byte[] generateReportByte(String barcode) throws JRException {
		// generateReport(barcode);
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	@Override
	public void generateReport(String reportName) throws JRException {
		JasperExportManager.exportReportToPdfFile(jasperPrint,
				"/root/" + "barcode_" + System.currentTimeMillis() + ".pdf");
	}

}
