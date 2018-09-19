package com.pos.retailer.controller;

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

import com.pos.retailer.component.ResponseWrapper;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.CustomerDetails;
import com.pos.retailer.service.CustomerService;

@RestController
@RequestMapping("/customer")
public class CustomerCotroller {

	@Autowired
	private CustomerService customerService;

	@GetMapping("/{custType}")
	public ResponseEntity<?> getAllCustomerOfCustType(@PathVariable String custType) {
		return new ResponseWrapper<>("Party Details", HttpStatus.OK,
				customerService.getAllPartyDetailsOfCustType(custType)).sendResponse();
	}

	@PostMapping
	public ResponseEntity<?> saveCustomer(@RequestBody CustomerDetails customer) throws GenericException {
		return new ResponseWrapper<>("Customer Details Saved", HttpStatus.OK,
				this.customerService.saveCustomer(customer)).sendResponse();
	}

	@DeleteMapping
	public ResponseEntity<?> deleteCustomer(@RequestBody CustomerDetails customer) throws GenericException {
		return new ResponseWrapper<>("Customer Details Saved", HttpStatus.OK, null).sendResponse();
	}

}
