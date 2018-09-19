package com.pos.retailer.controller;

import static com.pos.retailer.component.AppConstant.chckInvSts;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pos.retailer.component.ResponseWrapper;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.Product;
import com.pos.retailer.service.InventoryOrderDetailsService;
import com.pos.retailer.service.ProductService;

@RestController
@RequestMapping("/products")

public class ProductController {

	Logger logger = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	ProductService productService;

	@Autowired
	InventoryOrderDetailsService inventoryOrderDetailsService;

	@GetMapping()
	public ResponseEntity<?> getAllProduct() {
		return new ResponseWrapper<List<Product>>(HttpStatus.OK, this.productService.getAllProducts()).sendResponse();
	}

	@GetMapping("/allNames")
	public ResponseEntity<?> getAllProductNames() {
		return new ResponseWrapper<>(HttpStatus.OK, this.productService.getAllProductNames()).sendResponse();
	}

	@GetMapping("/barcode/{barcode}")
	public ResponseEntity<?> getProductByBarcode(@PathVariable String barcode) {
		return new ResponseWrapper<>(HttpStatus.OK, this.productService.getProductByBarcode(barcode)).sendResponse();
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<?> getProductByName(@PathVariable String name) {
		return new ResponseWrapper<>(HttpStatus.OK, this.productService.getProductByName(name)).sendResponse();
	}

	@PostMapping
	public ResponseEntity<?> saveProduct(@RequestBody Product product) throws GenericException {
		chckInvSts();
		return new ResponseWrapper<Product>(HttpStatus.OK, this.productService.saveProduct(product)).sendResponse();
	}

//	@GetMapping("/category")
//	public ResponseEntity<?> getDistinctProductCategory() {
//		return new ResponseWrapper<>(HttpStatus.OK, this.productService.getDistinctCategory()).sendResponse();
//	}
//	
//	@GetMapping("/category/{categoryName}")
//	public ResponseEntity<?> getPrdocutByCategory(@PathVariable String categoryName) {
//		return new ResponseWrapper<>(HttpStatus.OK, this.productService.getProductsByCategory(categoryName)).sendResponse();
//	}
	
	
	
	
	@GetMapping("/{barcode}/{status}")
	public ResponseEntity<?> setProductAvailStaus(@PathVariable String barcode, @PathVariable boolean status)
			throws GenericException {

		this.productService.updateAavilabilityStatus(barcode, status);
		return new ResponseWrapper<>("Staus Updated", HttpStatus.OK, null).sendResponse();
	}

	/*@GetMapping("barcode/{barcode}/download")
	public ResponseEntity<Resource> download(String param) throws IOException {
		File file = new File("D:/out.png");
		Path path = Paths.get(file.getAbsolutePath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");

		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}*/
	
	// Transaction

	@GetMapping("/inventory/status")
	public ResponseEntity<?> checkInventrySatus() {
		return new ResponseWrapper<>(HttpStatus.OK, productService.checkInvnetoryClosingStatus()).sendResponse();
	}

	@GetMapping("/inventory/open")
	public ResponseEntity<?> openInventory() throws GenericException {
		productService.openInnventory();
		return new ResponseWrapper<>("Invnetory Opened", HttpStatus.OK, null).sendResponse();
	}

	@GetMapping("/inventory/close")
	public ResponseEntity<?> closeInvnetory() throws GenericException {
		productService.closeInventory();
		return new ResponseWrapper<>("Invnetory Closed", HttpStatus.OK, null).sendResponse();
	}

}
