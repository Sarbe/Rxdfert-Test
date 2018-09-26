package com.pos.retailer.service;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.report.OutStandingReport.OutstandingSummary;
import com.pos.retailer.report.StockReport.StockInput;

public interface ReportService {

	byte[] getInvoiceReport(String orderId, ApplicationContext context) throws Exception;

	StockInput getStockHistory(StockInput input) throws GenericException, Exception;

	List<OutstandingSummary> getOutstandingDetails(String requestType, String viewType);


}