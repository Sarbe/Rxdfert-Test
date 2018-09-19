package com.pos.retailer.controller;

import static com.pos.retailer.component.AppConstant.chckInvSts;

import java.security.Principal;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.component.ResponseWrapper;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.InventoryOrder;
import com.pos.retailer.model.PaymentDetails;
import com.pos.retailer.model.SalesDto;
import com.pos.retailer.model.SalesOrder;
import com.pos.retailer.model.SalesOrderDto;
import com.pos.retailer.model.SellerDetails;
import com.pos.retailer.report.InvoiceBillReport.InvoiceDto;
import com.pos.retailer.service.MasterDataService;
import com.pos.retailer.service.PaymentHistoryService;
import com.pos.retailer.service.SalesOrderDetailsService;
import com.pos.retailer.service.SalesOrderService;

@RestController
@RequestMapping("/sales")
public class SalesOrderController {

	Logger logger = LoggerFactory.getLogger(SalesOrderController.class);

	@Autowired
	SalesOrderService salesOrderService;

	@Autowired
	SalesOrderDetailsService salesOrderDetailsService;

	@Autowired
	PaymentHistoryService paymentHistoryService;

	@Autowired
	MasterDataService masterDataService;

	@GetMapping("/customers")
	public ResponseEntity<?> getAllCustomer() {
		return new ResponseWrapper<>("All Customer Details", HttpStatus.OK,
				salesOrderService.getUniqueCustomerDetails()).sendResponse();
	}

	@GetMapping("/customers/{contactNbr}")
	public ResponseEntity<?> getCustomerDetailsByContactNumber(@PathVariable String contactNbr)
			throws GenericException {
		return new ResponseWrapper<>(HttpStatus.OK, salesOrderService.getCustomerDetailsByContactNbr(contactNbr))
				.sendResponse();

	}

	// Sales Order API
	@GetMapping()
	public ResponseEntity<?> getAllSalesOrders() {
		return new ResponseWrapper<>(HttpStatus.OK, this.salesOrderService.getAllSalesOrders()).sendResponse();
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<?> getAllOrdersByStatus(@PathVariable String... status) {
		return new ResponseWrapper<>(HttpStatus.OK,
				this.salesOrderService.getAllSalesOrdersByStatus(Arrays.asList(status))).sendResponse();
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<?> getSalesOrder(@PathVariable String orderId) throws GenericException {
		return new ResponseWrapper<>(HttpStatus.OK, this.salesOrderService.getFullSalesDetails(orderId)).sendResponse();
	}

	@GetMapping("/orderId")
	public ResponseEntity<?> generateOrderId() throws GenericException {
		// chckInvSts();
		return new ResponseWrapper<>(HttpStatus.OK, this.salesOrderService.generateOrderId()).sendResponse();
	}

	@PostMapping
	public ResponseEntity<?> saveSalesOrder(@RequestBody SalesOrder saleOrder) throws GenericException {
		chckInvSts();
		return new ResponseWrapper<>("Purchase Order saved", HttpStatus.OK,
				this.salesOrderService.saveSalesOrder(saleOrder)).sendResponse();
	}

	@DeleteMapping("/{orderId}")
	public ResponseEntity<?> deleteSalesOrder(@PathVariable String orderId) throws GenericException {
		chckInvSts();
		salesOrderService.deleteSalesOrder(orderId);
		return new ResponseWrapper<>(HttpStatus.OK, null).sendResponse();
	}

	@GetMapping("/{orderId}/receipt")
	public ResponseEntity<?> receipt(Principal p, @PathVariable String orderId) throws GenericException {

		SalesOrderDto sdto = salesOrderService.getFullSalesDetails(orderId);
		if (!sdto.getSales().getOrderSts().equals(AppConstant.ORDER_CONFIRMED)) {
			throw new GenericException("Order yet has not been placed");
		}

		String userName = StringUtils.trimToEmpty(p.getName()).toUpperCase();
		SellerDetails seller = masterDataService.getSellerDetails();
		InvoiceDto invDto = new InvoiceDto(userName, seller, sdto.getSales(), sdto.getSalesDetails());

		return new ResponseWrapper<>(HttpStatus.OK, invDto).sendResponse();
	}

	@PostMapping("/{orderId}/confirm")
	private ResponseEntity<?> confirmSalesOrder(@PathVariable String orderId, @RequestBody SalesOrder salesOrder)
			throws GenericException {
		chckInvSts();

		return new ResponseWrapper<>("Purchase Confirmed", HttpStatus.OK,
				salesOrderService.confirmSalesOrder(orderId, salesOrder)).sendResponse();
	}

	@PostMapping("/{orderId}/payment")
	public ResponseEntity<?> payment(Principal p, @PathVariable String orderId, @RequestBody PaymentDetails payment)
			throws GenericException {
		chckInvSts();

		String userName = StringUtils.trimToEmpty(p.getName()).toUpperCase();
		SalesOrderDto sdto = this.salesOrderService.receivePayment(orderId, payment);
		SellerDetails seller = masterDataService.getSellerDetails();
		InvoiceDto invDto = new InvoiceDto(userName, seller, sdto.getSales(), sdto.getSalesDetails());
		return new ResponseWrapper<>("Payment Details updated.", HttpStatus.OK, invDto).sendResponse();
	}

	@PostMapping("/{orderId}/partPayment")
	public ResponseEntity<?> partPayment(@PathVariable String orderId, @RequestBody PaymentDetails payment)
			throws GenericException {

		return new ResponseWrapper<>("Payment Details updated.", HttpStatus.OK,
				this.salesOrderService.updatePaymentDetails(orderId, payment)).sendResponse();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	//// Product Order Details API

	@GetMapping("/{orderId}/orderDetails")
	private ResponseEntity<?> getSalesOrderDetails(@PathVariable String orderId) {
		return new ResponseWrapper<>(HttpStatus.OK, salesOrderDetailsService.getOrderDetailsByOrderId(orderId))
				.sendResponse();

	}

	/*
	 * @GetMapping("/{orderId}/{barcode}/save") private ResponseEntity<?>
	 * saveOrderDetail(@PathVariable String orderId, @PathVariable String barcode)
	 * throws GenericException { return new ResponseWrapper<>(HttpStatus.OK,
	 * salesOrderDetailsService.saveOrderDetail(orderId, barcode)) .sendResponse();
	 * }
	 */

	@DeleteMapping("/{orderId}/{orderDetailId}")
	public ResponseEntity<?> deleteOrderDetail(@PathVariable String orderId, @PathVariable Long orderDetailId)
			throws GenericException {
		chckInvSts();
		this.salesOrderDetailsService.deleteOrderDetailById(orderId, orderDetailId);
		return new ResponseWrapper<InventoryOrder>("Order Details Deleted", HttpStatus.OK, null).sendResponse();
	}

	@GetMapping("/{orderId}/{orderDtlId}/change/{changeType}")
	private ResponseEntity<?> changeOrderQuantity(@PathVariable String orderId, @PathVariable Long orderDtlId,
			@PathVariable String changeType) throws GenericException {
		chckInvSts();
		return new ResponseWrapper<>(HttpStatus.OK,
				salesOrderDetailsService.changeQtyByOne(orderId, orderDtlId, changeType)).sendResponse();
	}

	@GetMapping("/{orderId}/{orderDtlId}/{qty}")
	private ResponseEntity<?> changeOrderQuantity(@PathVariable String orderId, @PathVariable Long orderDtlId,
			@PathVariable int qty) throws GenericException {
		chckInvSts();
		return new ResponseWrapper<>(HttpStatus.OK, salesOrderDetailsService.changeQty(orderId, orderDtlId, qty))
				.sendResponse();
	}

	@PostMapping("/saveDetails")
	private ResponseEntity<?> saveDetail(@RequestBody SalesDto sdto) throws GenericException {
		chckInvSts();
		return new ResponseWrapper<>(HttpStatus.OK,
				salesOrderDetailsService.saveProductByBarcodeAndQty(sdto.getOrderId(), sdto.getBarcode(), 1))
						.sendResponse();
	}

	//////////////
	@GetMapping("/{orderId}/paymentHistory")
	private ResponseEntity<?> getOrderPaymentHistory(@PathVariable String orderId) {
		return new ResponseWrapper<>(HttpStatus.OK, paymentHistoryService.getPaymentHistory(orderId, AppConstant.SALES))
				.sendResponse();
	}
}
