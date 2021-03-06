package com.pos.retailer.service.Impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.InventoryTransaction;
import com.pos.retailer.model.InventoryTranscSummary;
import com.pos.retailer.model.SalesOrder;
import com.pos.retailer.model.SalesOrderDto;
import com.pos.retailer.model.SellerDetails;
import com.pos.retailer.report.InvoiceBillReport;
import com.pos.retailer.report.OutStandingReport.OutstandingSummary;
import com.pos.retailer.report.ReportUtility;
import com.pos.retailer.report.StockReport.StockInput;
import com.pos.retailer.repository.InventoryOrderRepository;
import com.pos.retailer.repository.SalesOrderRepository;
import com.pos.retailer.repository.model.CustomOutstanding;
import com.pos.retailer.service.MasterDataService;
import com.pos.retailer.service.ProductService;
import com.pos.retailer.service.ReportService;
import com.pos.retailer.service.SalesOrderDetailsService;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

	@Autowired
	private SalesOrderRepository salesOrderRepository;

	@Autowired
	private SalesOrderDetailsService salesOrderDetailsService;

	@Autowired
	private InventoryOrderRepository inventoryRepository;

	// @Autowired
	// private InventoryOrderDetailsService inventoryOrderDetailsService;

	@Autowired
	private MasterDataService masterDataService;

	@Autowired
	private ProductService productService;

	@Override
	public byte[] getInvoiceReport(String orderId, ApplicationContext ctx) throws Exception {

		// order
		SellerDetails seller = masterDataService.getSellerDetails();
		if (seller == null) {
			throw new GenericException("Seller Details not found");
		}

		SalesOrder salesOrder = salesOrderRepository.findByOrderId(orderId)
				.orElseThrow(() -> new GenericException("order details not found."));

		if (!StringUtils.trimToEmpty(salesOrder.getOrderSts()).equals(AppConstant.ORDER_CONFIRMED)) {
			throw new GenericException("Order yet not confirmed.");
		}

		/*
		 * if (StringUtils.isEmpty(salesOrder.getReceiptNumber())) { Long seq =
		 * sequenceGeneratorService.getNextValue("invoiceNbr"); String receiprtNbr =
		 * "INV/1" + StringUtils.leftPad(seq.toString(), 10, "0");
		 * salesOrder.setReceiptNumber(receiprtNbr);
		 * salesOrderRepository.save(salesOrder); }
		 */
		// order Dto
		SalesOrderDto sdto = new SalesOrderDto();
		sdto.setSales(salesOrder);
		sdto.setSalesDetails(salesOrderDetailsService.getOrderDetailsByOrderId(orderId));

		ReportUtility report = new InvoiceBillReport();
		report.prepareReport("", sdto, seller);
		return report.generateReportByte(orderId);
	}

	@Override
	public StockInput getStockHistory(StockInput input) throws Exception {

		if (input.getStartDt() == null && input.getEndDt() == null) {
			throw new GenericException("Please enter date period");
		}
		if (input.getEndDt().isBefore(input.getStartDt())) {
			throw new GenericException("End Date Cannot be before start Date.");
		}

		LocalDate startDate = input.getStartDt();
		LocalDate endDate = input.getEndDt();

		List<InventoryTranscSummary> summary = new ArrayList<>();

		List<InventoryTransaction> transactions = productService.getTracsactionDataNative(startDate, endDate);

		if (transactions != null && !transactions.isEmpty()) {

			InventoryTranscSummary its = null;
			InventoryTransaction row = null;
			for (int i = 0; i < transactions.size(); i++) {
				row = transactions.get(i);

				its = new InventoryTranscSummary();
				its.setBarcode(row.getBarcode());

				boolean isNew = true;
				if (summary.contains(its)) {
					its = summary.get(summary.indexOf(its));
					isNew = false;
				} else {
					its.setPrdName(row.getProductName());
					its.setUom(row.getUom());
				}

				if (row.getActivity().equals(AppConstant.STOCK_OPEN)) {
					if (its.getOpeningStock() == -1)
						its.setOpeningStock(row.getQty());
				} else if (row.getActivity().equals(AppConstant.STOCK_CLOSE)) {
					its.setCloseStock(row.getQty());
				} else if (row.getActivity().equals(AppConstant.STOCK_IN)) {
					its.increaseStockInQty(row.getQty());
				} else if (row.getActivity().equals(AppConstant.STOCK_OUT)) {
					its.increaseStockOutQty(row.getQty());
				} else if (row.getActivity().equals(AppConstant.STOCK_DUMP)) {
					its.increaseDumpsQty(its.getDumpStock());
				}

				if (isNew) {
					summary.add(its);
				} else {
					summary.set(summary.indexOf(its), its);
				}
			}
		}

		SellerDetails seller = masterDataService.getSellerDetails();
		input.setDataList(summary);
		input.setSeller(seller);

		return input;
	}

	@Override
	public List<OutstandingSummary> getOutstandingDetails(String orderType, String viewType) {

		List<CustomOutstanding> dbSummary = new ArrayList<>();

		if (orderType.equals(AppConstant.PURCHASE)) {
			// get details for seller outstanding
			if (viewType.equals(AppConstant.REPORT_VIEW))
				dbSummary = inventoryRepository.findOutstandingsForAll();
			else
				dbSummary = inventoryRepository.findDetailedOutstandingForAll();

		} else if (orderType.equals(AppConstant.SALES)) {
			// get details for customer outstanding
			if (viewType.equals(AppConstant.REPORT_VIEW))
				dbSummary = salesOrderRepository.findOutstandingsForAll();
			else
				dbSummary = salesOrderRepository.findDetailedOutstandingForAll();

		}

		List<OutstandingSummary> summaryList = new ArrayList<>();

		for (CustomOutstanding summary : dbSummary) {
			OutstandingSummary os = new OutstandingSummary();
			os.setContactNbr(summary.getContactNbr());
			os.setOrderId(summary.getOrderId());
			os.setOutstanding(summary.getOutstanding());
			os.setDiscount(summary.getDiscount());
			os.setPartyName(summary.getPartyName());
			os.setTotalAmount(summary.getTotalAmount());
			os.setOrderType(summary.getOrderType());
			summaryList.add(os);
		}

		return summaryList;

	}
}
