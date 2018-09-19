package com.pos.retailer.report;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.pos.retailer.model.InventoryTranscSummary;
import com.pos.retailer.model.SellerDetails;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Component
public class StockReport extends ReportUtility {

	@Override
	public void reportConstructor(Object... inputData) throws Exception {
		StockInput input = (StockInput) inputData[0];
		reportParam.put("input", input);
		reportParam.put("seller", input.getSeller());
	}

	@Override
	public void reportMetadata(String reportParams) throws JRException, IOException {

		InputStream resource = new ClassPathResource("Stock.jasper").getInputStream();
		jasperReport = (JasperReport) JRLoader.loadObject(resource);

		// jasperReport = JasperCompileManager.compileReport("Stock.jrxml");
	}

	@Override
	public void fillReport() throws JRException {
		jasperPrint = JasperFillManager.fillReport(jasperReport, reportParam, new JREmptyDataSource());
	}

	@Override
	public byte[] generateReportByte(String reportName) throws JRException {
		// generateReport(reportName);
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	@Override
	public void generateReport(String reportName) throws JRException {
		JasperExportManager.exportReportToPdfFile(jasperPrint,
				"/root/" + reportName + "_" + System.currentTimeMillis() + ".pdf");
	}

	// extra class
	@Getter
	@Setter
	@NoArgsConstructor
	// @JsonInclude(Include.NON_EMPTY)
	public static class StockInput {
		private LocalDate startDt;
		private LocalDate endDt;

		private SellerDetails seller = new SellerDetails();
		List<InventoryTranscSummary> dataList = new ArrayList<>();
	}

}
