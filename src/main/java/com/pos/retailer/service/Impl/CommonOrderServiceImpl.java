package com.pos.retailer.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.controller.InventoryOrderController;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.CountBean;
import com.pos.retailer.model.InventoryOrder;
import com.pos.retailer.model.MultiPayment;
import com.pos.retailer.model.PaymentDetails;
import com.pos.retailer.model.SalesOrder;
import com.pos.retailer.report.OutStandingReport.OutstandingSummary;
import com.pos.retailer.repository.InventoryOrderRepository;
import com.pos.retailer.repository.ProductRepository;
import com.pos.retailer.repository.SalesOrderRepository;
import com.pos.retailer.repository.model.CustomOutstanding;
import com.pos.retailer.service.CommonOrderService;
import com.pos.retailer.service.InventoryOrderService;
import com.pos.retailer.service.SalesOrderService;

@Service
public class CommonOrderServiceImpl implements CommonOrderService {

	Logger logger = LoggerFactory.getLogger(InventoryOrderController.class);

	@Autowired
	private SalesOrderRepository salesOrderRepository;

	@Autowired
	private SalesOrderService salesOrderService;

	@Autowired
	private InventoryOrderRepository inventoryOrderRepository;

	@Autowired
	private InventoryOrderService inventoryOrderService;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public InventoryOrder getInventoryOrderByOrderId(String orderId) throws GenericException {

		Optional<InventoryOrder> optInvOrder = inventoryOrderRepository.findByOrderId(orderId);
		if (!optInvOrder.isPresent())
			throw new GenericException("Order not present.");
		return optInvOrder.get();
	}

	@Override
	public SalesOrder getSalesOrderByOrderId(String orderId) throws GenericException {

		Optional<SalesOrder> optSalesOrder = salesOrderRepository.findByOrderId(orderId);
		if (!optSalesOrder.isPresent())
			throw new GenericException("Order not present.");
		return optSalesOrder.get();
	}

	///////////////

	@Override
	public CountBean getCounts() {
		CountBean cb = new CountBean();

		cb.setSalesAll((int) salesOrderRepository.count());
		cb.setSalesNew(salesOrderRepository.findByOrderStsOrderByActivityDtDesc(AppConstant.ORDER_NEW).size());
		cb.setSalesSettled(salesOrderRepository.findBySettled(true).size());
		cb.setSalesUnSettled(salesOrderRepository.findBySettled(false).size());

		cb.setPurchaseAll((int) inventoryOrderRepository.count());
		cb.setPurchaseNew(inventoryOrderRepository.findByOrderStsOrderByActivityDtDesc(AppConstant.ORDER_NEW).size());
		cb.setPurchaseSettled(inventoryOrderRepository.findBySettled(true).size());
		cb.setPurchaseUnSettled(inventoryOrderRepository.findBySettled(false).size());

		cb.setProductAll((int) productRepository.count());
		cb.setProductAvail(productRepository.findByAvailability(true).size());
		cb.setProductNotAvail(productRepository.findByAvailability(false).size());
		cb.setStockLess(productRepository.findByThresholdQtyLessThanStockQty().size());
		return cb;

	}

	@Override
	public void getOrderNbrs() {
		String[] orders = inventoryOrderRepository.findEmptyOrders();
		System.out.println(orders.length);

	}

	//////////////////
	/////////////////

	@Override
	public List<OutstandingSummary> getDetailedOutstandingsForOneParty(String orderType, String partyName) {

		List<CustomOutstanding> detailedOutstanding = new ArrayList<>();

		if (orderType.equals(AppConstant.PURCHASE)) {
			// get details for seller outstanding
			detailedOutstanding = inventoryOrderRepository.findDetailedOutstandingForOneParty(partyName);

		} else if (orderType.equals(AppConstant.SALES)) {
			// get details for customer outstanding
			detailedOutstanding = salesOrderRepository.findDetailedOutstandingForOneParty(partyName);
		}

		List<OutstandingSummary> summaryList = customToToalOutStanding(detailedOutstanding);

		return summaryList;
	}

	public List<OutstandingSummary> getOutstandingsForOneParty(String orderType, String partyName) {

		List<CustomOutstanding> detailedOutstanding = new ArrayList<>();

		if (orderType.equals(AppConstant.PURCHASE)) {
			// get details for seller outstanding
			detailedOutstanding = inventoryOrderRepository.findOutstandingForOneParty(partyName);

		} else if (orderType.equals(AppConstant.SALES)) {
			// get details for customer outstanding
			detailedOutstanding = salesOrderRepository.findOutstandingForOneParty(partyName);
		}

		List<OutstandingSummary> summaryList = customToToalOutStanding(detailedOutstanding);

		return summaryList;
	}

	private List<OutstandingSummary> customToToalOutStanding(List<CustomOutstanding> detailedOutstanding) {
		List<OutstandingSummary> summaryList = new ArrayList<>();

		for (CustomOutstanding summary : detailedOutstanding) {
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

	@Override
	public List<OutstandingSummary> multiplePayment(MultiPayment payment) throws GenericException {

		double receivedAmount = payment.getAmount();
		if (receivedAmount == 0) {
			throw new GenericException("Entered Amount can not be 0");
		}

		List<OutstandingSummary> summary = getDetailedOutstandingsForOneParty(payment.getOrderType(),
				payment.getPartyName());

		for (OutstandingSummary outstanding : summary) {
			if (receivedAmount > 0) {
				double paidAmount = 0;
				if (outstanding.getOutstanding() > receivedAmount) {
					paidAmount = receivedAmount;
				} else {
					paidAmount = outstanding.getOutstanding();
				}

				if (StringUtils.trimToEmpty(payment.getOrderType()).equals(AppConstant.SALES)) {
					salesOrderService.updatePaymentDetails(outstanding.getOrderId(),
							new PaymentDetails(paidAmount, payment.getPaymentMode()));
				} else if (StringUtils.trimToEmpty(payment.getOrderType()).equals(AppConstant.PURCHASE)) {
					inventoryOrderService.updatePaymentDetails(outstanding.getOrderId(),
							new PaymentDetails(paidAmount, payment.getPaymentMode()));
				}

				receivedAmount -= paidAmount;
				if (receivedAmount == 0) {
					break;
				}

			}
		}

		List<OutstandingSummary> detailedOutstanding = getOutstandingsForOneParty(payment.getOrderType(),
				payment.getPartyName());

		return detailedOutstanding;

	}

}
