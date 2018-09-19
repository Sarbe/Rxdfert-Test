package com.pos.retailer.controller;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.component.ResponseWrapper;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.Product;
import com.pos.retailer.model.SellerDetails;
import com.pos.retailer.report.BarCodeReport;
import com.pos.retailer.report.OutStandingReport;
import com.pos.retailer.report.OutStandingReport.OutstandingSummary;
import com.pos.retailer.report.ReportUtility;
import com.pos.retailer.report.StockReport;
import com.pos.retailer.report.StockReport.StockInput;
import com.pos.retailer.service.MasterDataService;
import com.pos.retailer.service.ProductService;
import com.pos.retailer.service.ReportService;

@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	private ProductService productService;

	@Autowired
	private ReportService reportService;

	@Autowired
	private MasterDataService masterDataService;

	@Autowired
	ApplicationContext context;

	@GetMapping("/barcode/{barcode}")
	public ResponseEntity<?> download(@PathVariable String barcode) throws Exception {

		Product product = productService.getProductByBarcode(barcode);
		if (ObjectUtils.isEmpty(product)) {
			throw new GenericException("Invalid product.");
		}

		BarCodeReport report = new BarCodeReport();
		report.prepareReport("", product.getBarcode(), product.getProductName());
		byte[] b = report.generateReportByte("");
		ByteArrayResource resource = new ByteArrayResource(b);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=" + barcode + "_" + System.currentTimeMillis() + ".pdf");
		headers.set("charset", "utf-8");
		headers.setContentLength(b.length);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return ResponseEntity.ok().headers(headers).body(resource);

	}

	@GetMapping("/invoice/{orderId}")
	public ResponseEntity<?> invoice(@PathVariable String orderId) throws Exception {
		byte[] b = reportService.getInvoiceReport(orderId, context);

		ByteArrayResource resource = new ByteArrayResource(b);

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=" + orderId + "_" + System.currentTimeMillis() + ".pdf");
		headers.set("charset", "utf-8");
		headers.setContentLength(b.length);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

		return ResponseEntity.ok().headers(headers).body(resource);
	}

	@GetMapping("/stock/{viewType}/{startDt}/{endDt}")
	public ResponseEntity<?> stockReport(@PathVariable String viewType, // @RequestBody StockInput input)
			@PathVariable String startDt, @PathVariable String endDt) throws Exception {

		StockInput input = new StockInput();
		input.setStartDt(LocalDate.parse(startDt));
		input.setEndDt(LocalDate.parse(endDt));
		reportService.getStockHistory(input);

		if (viewType.equals(AppConstant.REPORT_DOWNLOAD)) {
			StockReport sr = new StockReport();
			sr.prepareReport("", input);
			byte[] b = sr.generateReportByte("transac");

			ByteArrayResource resource = new ByteArrayResource(b);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=" + "transaction" + "_" + System.currentTimeMillis() + ".pdf");
			headers.set("charset", "utf-8");
			headers.setContentLength(b.length);
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			return ResponseEntity.ok().headers(headers).body(resource);
		} else {
			return new ResponseWrapper<>(HttpStatus.OK, input).sendResponse();
		}
	}

	@GetMapping("/outstanding/{viewType}/{requestType}")
	public ResponseEntity<?> outstanding(@PathVariable String requestType, @PathVariable String viewType)
			throws Exception {
		
		requestType = StringUtils.trimToEmpty(requestType).equals(AppConstant.PURCHASE) ? AppConstant.PURCHASE
				: AppConstant.SALES;

		List<OutstandingSummary> summary = reportService.getOutstandingDetails(requestType);
		SellerDetails sd = masterDataService.getSellerDetails();
		if (viewType.equals(AppConstant.REPORT_DOWNLOAD)) {

			ReportUtility osReport = new OutStandingReport();
			osReport.prepareReport("", summary, sd, requestType);
			byte[] b = osReport.generateReportByte(requestType + "_outstanding");

			ByteArrayResource resource = new ByteArrayResource(b);
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=" + requestType + "_outstanding" + "_" + System.currentTimeMillis() + ".pdf");
			headers.set("charset", "utf-8");
			headers.setContentLength(b.length);
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			return ResponseEntity.ok().headers(headers).body(resource);
		} else {
			return new ResponseWrapper<>(HttpStatus.OK, summary).sendResponse();
		}
	}

}
