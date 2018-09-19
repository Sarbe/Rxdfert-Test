package com.pos.retailer.controller;

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

import com.pos.retailer.component.ResponseWrapper;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.SellerDetails;
import com.pos.retailer.service.MasterDataService;

@RestController
@RequestMapping("/data")

public class MasterDataController {

	Logger logger = LoggerFactory.getLogger(MasterDataController.class);

	@Autowired
	MasterDataService masterDataService;

	@GetMapping("/category/{category}")
	public ResponseEntity<?> getProductByBarcode(@PathVariable String category) {
		return new ResponseWrapper<>("UOM Details", HttpStatus.OK, this.masterDataService.getDataByCategory(category))
				.sendResponse();
	}

	// Save Seller Details

	@GetMapping("/seller")
	public ResponseEntity<?> getSellerDetails() throws GenericException {
		return new ResponseWrapper<>("Seller Details fetched", HttpStatus.OK,
				this.masterDataService.getSellerDetails()).sendResponse();
	}

	@PostMapping("/seller")
	public ResponseEntity<?> saveSellerDetails(@RequestBody SellerDetails seller) {
		return new ResponseWrapper<>("Seller Details Saved", HttpStatus.OK,
				this.masterDataService.saveSellerDetails(seller)).sendResponse();
	}

	@DeleteMapping("/seller/{id}")
	public ResponseEntity<?> deleteSellerDetails(@PathVariable Long id) {
		this.masterDataService.deleteSellerDetails(id);
		return new ResponseWrapper<>("Seller Details Deleted", HttpStatus.OK, "").sendResponse();
	}

}
