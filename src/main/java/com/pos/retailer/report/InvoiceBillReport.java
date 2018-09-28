package com.pos.retailer.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.pos.retailer.model.MasterData;
import com.pos.retailer.model.SalesOrder;
import com.pos.retailer.model.SalesOrderDetails;
import com.pos.retailer.model.SalesOrderDto;
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
public class InvoiceBillReport extends ReportUtility {

	@Override
	public void reportConstructor(Object... inputData) throws Exception {

		SalesOrderDto invDto = (SalesOrderDto) inputData[0];
		reportParam.put("ORDER", invDto);

		SellerDetails seller = (SellerDetails) inputData[1];
		reportParam.put("SELLER", seller);

		// reportParam.put("CONTEXT",context.getResource("/").getURI().getPath());
	}

	@Override
	public void reportMetadata(String reportParams) throws JRException, IOException {

		InputStream resource = new ClassPathResource("InvoiceBill.jasper").getInputStream();
		jasperReport = (JasperReport) JRLoader.loadObject(resource);

		// jasperReport = JasperCompileManager.compileReport("invoiceBill.jrxml");
	}

	@Override
	public void fillReport() throws JRException {
		jasperPrint = JasperFillManager.fillReport(jasperReport, reportParam, new JREmptyDataSource());

	}

	@Override
	public void generateReport(String reportName) throws JRException {
		JasperExportManager.exportReportToPdfFile(jasperPrint,
				"/root/" + reportName + "_" + System.currentTimeMillis() + ".pdf");
	}

	@Override
	public byte[] generateReportByte(String reportName) throws JRException {
		// generateReport(reportName);
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class InvoiceDto implements Serializable{

		private static final long serialVersionUID = 1L;
		
		private String loggedInUser;
		private SellerDetails sellerDetails = new SellerDetails();
		private SalesOrder sales;
		private List<SalesOrderDetails> salesDetails = new ArrayList<>();
		private List<MasterData> tnc = new ArrayList<>();


		public InvoiceDto(String loggedInUser, SellerDetails sellerDetails, SalesOrder sales,
				List<SalesOrderDetails> salesDetails,List<MasterData> tnc) {
			super();
			this.loggedInUser = loggedInUser;
			this.sellerDetails = sellerDetails;
			this.sales = sales;
			this.salesDetails = salesDetails;
			this.tnc = tnc;
		}
		

	}

}
