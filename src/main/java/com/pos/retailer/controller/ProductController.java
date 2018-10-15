
package com.pos.retailer.controller;

import static com.pos.retailer.component.AppConstant.chckInvSts;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pos.retailer.component.ResponseWrapper;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.Product;
import com.pos.retailer.model.ProductSummaryDto;
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

		 ProductSummaryDto psd = new ProductSummaryDto();
		 psd.setProducts(this.productService.getAllProducts());
		 psd.setSummary(this.productService.getProductSummary());

		return new ResponseWrapper<>("All product details fetched", HttpStatus.OK, psd)//this.productService.getAllProducts())
				.sendResponse();
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

	@GetMapping("/{barcode}/{status}")
	public ResponseEntity<?> setProductAvailStaus(@PathVariable String barcode, @PathVariable boolean status)
			throws GenericException {

		this.productService.updateAavilabilityStatus(barcode, status);
		return new ResponseWrapper<>("Staus Updated", HttpStatus.OK, null).sendResponse();
	}

	@GetMapping("/category")
	public ResponseEntity<?> getDistinctProductCategory() {
		return new ResponseWrapper<>(HttpStatus.OK, this.productService.getDistinctCategory()).sendResponse();
	}

	@GetMapping("/category/{categoryName}")
	public ResponseEntity<?> getPrdocutsByCategory(@PathVariable String categoryName) {
		return new ResponseWrapper<>(HttpStatus.OK, this.productService.getProductsByCategory(categoryName))
				.sendResponse();
	}

	@GetMapping("/allPrdNames/{category}")
	public ResponseEntity<?> getAllProductNames(@PathVariable String category) {
		return new ResponseWrapper<>(HttpStatus.OK, this.productService.getAllProductNamesByCategory(category))
				.sendResponse();
	}

	@GetMapping("/allMfgNames")
	public ResponseEntity<?> getAllManufacturerNames() {
		return new ResponseWrapper<>(HttpStatus.OK, this.productService.getDistinctManufacturer()).sendResponse();
	}
	/*
	 * @GetMapping("barcode/{barcode}/download") public ResponseEntity<Resource>
	 * download(String param) throws IOException { File file = new
	 * File("D:/out.png"); Path path = Paths.get(file.getAbsolutePath());
	 * ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
	 * HttpHeaders headers = new HttpHeaders(); headers.add("Cache-Control",
	 * "no-cache, no-store, must-revalidate");
	 * headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
	 * file.getName()); headers.add("Pragma", "no-cache"); headers.add("Expires",
	 * "0");
	 * 
	 * return ResponseEntity.ok().headers(headers).contentLength(file.length())
	 * .contentType(MediaType.parseMediaType("application/octet-stream")).body(
	 * resource); }
	 */

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

	@PostMapping("/uploadFile")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws GenericException, IOException {
		String fileName = file.getOriginalFilename();
		if (!fileName.substring(fileName.lastIndexOf(".") + 1).equalsIgnoreCase("xlsx")) {
			throw new GenericException("Only file with Extention XLSX is allowed");
		}

		
		List<Product> products = productService.processFileData(file.getInputStream());
		List<Product> failedList = new ArrayList<>();
		
		for (Product product : products) {
			try {
				if (product.checkEmpty())
					throw new Exception("All mandatory data is not present.");
				productService.saveProduct(product);
			} catch (Exception e) {
				product.setErrMsg(e.getMessage());
				failedList.add(product);
			}
		}

		
		if (failedList.size() == 0) {
			return new ResponseWrapper<>("Data uploaded Successfully", HttpStatus.OK, null).sendResponse();
		} else {

//			byte b[] = productService.writeToXLSXFile(failedList);
//
//			ByteArrayResource resource = new ByteArrayResource(b);
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.add(HttpHeaders.CONTENT_DISPOSITION,
//					"attachment; filename=Failed_" + System.currentTimeMillis() + ".xlsx");
//			headers.set("charset", "utf-8");
//			headers.setContentLength(b.length);
//			headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
//
//			return ResponseEntity.ok().headers(headers).body(resource);
			
			return new ResponseWrapper<>("Some data got rejected.Please correct and upload those data only.", HttpStatus.OK, failedList).sendResponse();
		}
	}

}
