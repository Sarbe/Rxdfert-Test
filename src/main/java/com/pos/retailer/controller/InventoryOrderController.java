package com.pos.retailer.controller;

import java.util.Arrays;
import java.util.List;

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

import static com.pos.retailer.component.AppConstant.chckInvSts;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.component.ResponseWrapper;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.InventoryOrder;
import com.pos.retailer.model.InventoryOrderDetails;
import com.pos.retailer.model.PaymentDetails;
import com.pos.retailer.service.InventoryOrderDetailsService;
import com.pos.retailer.service.InventoryOrderService;
import com.pos.retailer.service.PaymentHistoryService;

@RestController
@RequestMapping("/purchase")
public class InventoryOrderController {

	Logger logger = LoggerFactory.getLogger(InventoryOrderController.class);

	@Autowired
	InventoryOrderService inventoryOrderService;

	@Autowired
	InventoryOrderDetailsService inventoryOrderDetailsService;

	@Autowired
	PaymentHistoryService paymentHistoryService;

	@GetMapping("/vendors")
	public ResponseEntity<?> getVendor() {
		return new ResponseWrapper<>("All vendor Details", HttpStatus.OK,
				inventoryOrderService.getUniqueVendorDetails()).sendResponse();
	}

	@GetMapping("/vendors/{contactNbr}")
	public ResponseEntity<?> getCustomerDetailsByContactNumber(@PathVariable String contactNbr)
			throws GenericException {
		return new ResponseWrapper<>(HttpStatus.OK, inventoryOrderService.getCustomerDetailsByContactNbr(contactNbr))
				.sendResponse();

	}

	// Product Order API
	@GetMapping()
	public ResponseEntity<?> getAllOrders() {
		return new ResponseWrapper<List<InventoryOrder>>(HttpStatus.OK, this.inventoryOrderService.getAllInvOrders())
				.sendResponse();
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<?> getAllOrdersByStatus(@PathVariable String ...status) {
		return new ResponseWrapper<List<InventoryOrder>>(HttpStatus.OK,
				this.inventoryOrderService.getAllInvOrdersByStatus(Arrays.asList(status))).sendResponse();
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<?> getInvOrder(@PathVariable String orderId) throws GenericException {
		return new ResponseWrapper<>(HttpStatus.OK, this.inventoryOrderService.getFullInvOrderById(orderId))
				.sendResponse();
	}

	@GetMapping("/orderId")
	public ResponseEntity<?> generateOrderId() throws GenericException {
		// chckInvSts();
		return new ResponseWrapper<>(HttpStatus.OK, this.inventoryOrderService.generateOrderId()).sendResponse();
	}

	@PostMapping
	public ResponseEntity<?> saveOrder(@RequestBody InventoryOrder order) throws GenericException {
		chckInvSts();
		return new ResponseWrapper<InventoryOrder>("Purchase Order saved", HttpStatus.OK,
				this.inventoryOrderService.saveInvOrder(order)).sendResponse();
	}

	@DeleteMapping("/{orderId}")
	public ResponseEntity<?> deleteOrder(@PathVariable String orderId) throws GenericException {
		chckInvSts();
		inventoryOrderService.deleteInvOrder(orderId);
		return new ResponseWrapper<>(HttpStatus.OK, null).sendResponse();
	}

	@PostMapping("/{orderId}/confirm")
	private ResponseEntity<?> confirmOrder(@PathVariable String orderId, @RequestBody InventoryOrder invOrder)
			throws GenericException {
		chckInvSts();
		return new ResponseWrapper<>("Purchase Confirmed", HttpStatus.OK,
				inventoryOrderService.confirmInventoryOrder(orderId, invOrder)).sendResponse();
	}

	@PostMapping("/{orderId}/payment")
	public ResponseEntity<?> payment(@PathVariable String orderId, @RequestBody PaymentDetails payment)
			throws GenericException {
		// chckInvSts();
		return new ResponseWrapper<>("Payment Details updated.", HttpStatus.OK,
				this.inventoryOrderService.receivePaymentDetails(orderId, payment)).sendResponse();
	}

	@PostMapping("/{orderId}/partPayment")
	public ResponseEntity<?> partPayment(@PathVariable String orderId, @RequestBody PaymentDetails payment)
			throws GenericException {
		// chckInvSts();
		return new ResponseWrapper<>("Payment Details updated.", HttpStatus.OK,
				this.inventoryOrderService.updatePaymentDetails(orderId, payment)).sendResponse();
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////
	//// Product Order Details API

	@GetMapping("/{orderId}/orderDetails")
	private ResponseEntity<?> getOrderDetails(@PathVariable String orderId) {
		return new ResponseWrapper<List<InventoryOrderDetails>>(HttpStatus.OK,
				inventoryOrderDetailsService.getOrderDetailsByOrderId(orderId)).sendResponse();

	}

	@PostMapping("/{orderId}/save")
	private ResponseEntity<?> saveOrderDetail(@PathVariable String orderId,
			@RequestBody InventoryOrderDetails orderDetail) throws GenericException {
		chckInvSts();
		return new ResponseWrapper<>(HttpStatus.OK, inventoryOrderDetailsService.saveOrderDetail(orderId, orderDetail))
				.sendResponse();
	}

	@DeleteMapping("/{orderId}/{orderDetailId}")
	public ResponseEntity<InventoryOrder> deleteOrderDetail(@PathVariable String orderId,
			@PathVariable Long orderDetailId) throws GenericException {
		chckInvSts();
		this.inventoryOrderDetailsService.deleteOrderDetailById(orderId, orderDetailId);
		return new ResponseWrapper<InventoryOrder>(HttpStatus.OK, null).sendResponse();
	}

	@GetMapping("/{orderId}/{orderDtlId}/change/{changeType}")
	private ResponseEntity<?> changeOrderQuantity(@PathVariable String orderId, @PathVariable Long orderDtlId,
			@PathVariable String changeType) throws GenericException {
		chckInvSts();
		return new ResponseWrapper<>(HttpStatus.OK,
				inventoryOrderDetailsService.changeQtyByOne(orderId, orderDtlId, changeType)).sendResponse();
	}

	//////////////
	@GetMapping("/{orderId}/paymentHistory")
	private ResponseEntity<?> getOrderPaymentHistory(@PathVariable String orderId) {
		return new ResponseWrapper<>(HttpStatus.OK,
				paymentHistoryService.getPaymentHistory(orderId, AppConstant.PURCHASE)).sendResponse();
	}

}
