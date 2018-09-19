package com.pos.retailer.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.pos.retailer.model.SearchCriteria;
import com.pos.retailer.repository.InventoryOrderRepository;
import com.pos.retailer.repository.ProductRepository;
import com.pos.retailer.repository.SalesOrderRepository;
import com.pos.retailer.repository.model.CustomOutstanding;
import com.pos.retailer.service.CommonOrderService;
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

	@Override
	public List<CustomOutstanding> getSummaryBySearchCriteriaGroupedBy(SearchCriteria search) throws GenericException {

		if(search.isEmpty()) {
			throw new GenericException("Please enter all search criteria");
		}
		
		List<CustomOutstanding> summary = new ArrayList<>();

		if (search.getOrderType().equals(AppConstant.SALES)) {
			if (search.getSearchType().equals(AppConstant.PARTY_NAME)) {
				summary = salesOrderRepository.findOutstandingsByPartyName(search.getSearchValue().toUpperCase());
			} else if (search.getSearchType().equals(AppConstant.CONTACT_NBR)) {
				summary = salesOrderRepository.findOutstandingsByContactNbr(search.getSearchValue());
			}
		} else {
			if (search.getSearchType().equals(AppConstant.PARTY_NAME)) {
				summary = inventoryOrderRepository.findOutstandingsByPartyName(search.getSearchValue());
			} else if (search.getSearchType().equals(AppConstant.CONTACT_NBR)) {
				summary = inventoryOrderRepository.findOutstandingsByContactNbr(search.getSearchValue());
			}
		}

		return summary;
	}
	
	
	public List<CustomOutstanding> getDetailsBySearchCriteriaGroupedBy(SearchCriteria search) {

		List<CustomOutstanding> summary = new ArrayList<>();

		if (search.getOrderType().equals(AppConstant.SALES)) {
			if (search.getSearchType().equals(AppConstant.PARTY_NAME)) {
				summary = salesOrderRepository.findOutstandingOrdersByPartyName(search.getSearchValue());
			} else if (search.getSearchType().equals(AppConstant.CONTACT_NBR)) {
				summary = salesOrderRepository.findOutstandingOrdersByContactNbr(search.getSearchValue());
			}
		} else {
			if (search.getSearchType().equals(AppConstant.PARTY_NAME)) {
				summary = inventoryOrderRepository.findOutstandingOrdersByPartyName(search.getSearchValue());
			} else if (search.getSearchType().equals(AppConstant.CONTACT_NBR)) {
				summary = inventoryOrderRepository.findOutstandingOrdersByContactNbr(search.getSearchValue());
			}
		}

		return summary;
	}

	@Override
	public List<CustomOutstanding> multiplePayment(MultiPayment payment) throws GenericException {

		List<CustomOutstanding> summary = getDetailsBySearchCriteriaGroupedBy(payment.getSearchCriteria());
		
		double receivedAmount = payment.getAmount();
		if (receivedAmount == 0) {
			throw new GenericException("Entered Amount can not be 0");
		}

		for (CustomOutstanding customOutstanding : summary) {
			if (receivedAmount > 0) {
				double paidAmount = 0;
				if (customOutstanding.getOutstanding() > receivedAmount) {
					paidAmount = receivedAmount;
				} else {
					paidAmount = customOutstanding.getOutstanding();
				}

				salesOrderService.updatePaymentDetails(customOutstanding.getOrderId(),
						new PaymentDetails(paidAmount, payment.getPaymentMode()));

				receivedAmount -= paidAmount;
				if (receivedAmount == 0) {
					break;
				}

			}
		}

		return getSummaryBySearchCriteriaGroupedBy(payment.getSearchCriteria());
	}

}
