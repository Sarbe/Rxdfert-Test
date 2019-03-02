package com.pos.retailer.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.InventoryTransaction;
import com.pos.retailer.model.Product;
import com.pos.retailer.repository.model.ProductSummary;

public interface ProductService {

	Product getProductById(String barcode) throws GenericException;

	Product getProductByBarcode(String barcode);

	Product saveProduct(Product product) throws GenericException;

	List<Product> getAllProducts();

	void deleteProduct(String barcode) throws GenericException;

	List<Product> getProductsByNameLike(String name);

	Product getProductByName(String name);

	List<String> getAllProductNamesByCategory(String category);

	void updateAavilabilityStatus(String barcode, boolean availability) throws GenericException;

	Product getProductByBarcodeAndAvilability(String barcode);

	/////////// Transaction
	void closeInventory() throws GenericException;

	void openInnventory() throws GenericException;

	void addInventoryTransactions(List<InventoryTransaction> transactions);

	void addInventoryTransaction(InventoryTransaction transactions);

	List<InventoryTransaction> getTransactionsByDate(LocalDate startDt, LocalDate endDate);

	boolean checkInvnetoryClosingStatus();

	List<InventoryTransaction> getTracsactionData(LocalDate startDate, LocalDate endDate);

	List<InventoryTransaction> getTracsactionDataNative(LocalDate startDate, LocalDate endDate) throws GenericException;

	List<String> getDistinctCategory();

	List<Product> getProductsByCategory(String categoryName, int pageNbr);
	
	List<String> getDistinctManufacturer();

	ProductSummary getProductSummary(String categoryName);

	List<Product> processFileData(InputStream is) throws GenericException;

	byte[] writeToXLSXFile(List<Product> products) throws GenericException;

}