package com.pos.retailer.report;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;

import com.pos.retailer.component.AppConstant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class OutStandingReport extends ReportUtility {

	@Override
	public void reportConstructor(Object... inputData) throws Exception {
		reportParam.put("summary", inputData[0]);
		reportParam.put("seller", inputData[1]);
		reportParam.put("type", inputData[2]);
	}

	@Override
	public void reportMetadata(String reportParams) throws JRException, IOException {
		
		
		InputStream resource = new ClassPathResource("Outstandings.jasper").getInputStream();
		jasperReport = (JasperReport) JRLoader.loadObject(resource);
		
		//jasperReport = JasperCompileManager.compileReport("Outstandings.jrxml");

	}

	@Override
	public void fillReport() throws JRException {
		jasperPrint = JasperFillManager.fillReport(jasperReport, reportParam, new JREmptyDataSource());
	}

	@Override
	public byte[] generateReportByte(String reportName) throws JRException {
	//	generateReport(reportName);
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

	@Override
	public void generateReport(String reportName) throws JRException {
		JasperExportManager.exportReportToPdfFile(jasperPrint,
				"/root/" + reportName + "_" + System.currentTimeMillis() + ".pdf");
	}

	// class

	@Getter
	@Setter
	@NoArgsConstructor
	public static class OutstandingSummary {

		private String partyName;
		private String contactNbr;
		private String orderId;
		private double totalAmount;
		private double discount;
		private double outstanding;
		private String orderType;

		public void setTotalAmount(double totalAmount) {
			this.totalAmount = AppConstant.roundedValue(totalAmount);
		}

		public void setDiscount(double discount) {
			this.discount = AppConstant.roundedValue(discount);
		}

		public void setOutstanding(double outstanding) {
			this.outstanding = AppConstant.roundedValue(outstanding);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((partyName == null) ? 0 : partyName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			OutstandingSummary other = (OutstandingSummary) obj;
			if (partyName == null) {
				if (other.partyName != null)
					return false;
			} else if (!partyName.equals(other.partyName))
				return false;
			return true;
		}

	}

}
