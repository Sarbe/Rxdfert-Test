package com.pos.retailer.service.Impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.InventoryTransaction;
import com.pos.retailer.model.Product;
import com.pos.retailer.repository.InventoryTransactionRepository;
import com.pos.retailer.repository.ProductRepository;
import com.pos.retailer.service.ProductService;
import com.pos.retailer.service.SequenceGeneratorService;
import com.pos.retailer.utility.EAN13Barcode;
import com.pos.retailer.utility.EAN8Barcode;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private InventoryTransactionRepository inventoryTransactionRepository;

	@Autowired
	SequenceGeneratorService sequenceGeneratorService;

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public List<String> getAllProductNamesByCategory(String category) {
		if (StringUtils.trimToEmpty(category).equals("ALL"))
			return productRepository.findAllProductName();
		else
			return productRepository.findAllProductNameByCatrgory(category);
	}

	@Override
	public List<String> getDistinctManufacturer() {
		return productRepository.findDistinctManufacturer();
	}

	@Override
	public Product getProductById(String barcode) throws GenericException {
		return this.productRepository.findByBarcode(barcode).orElseThrow(() -> new GenericException("Data not found."));
	}

	@Override
	public List<Product> getProductsByNameLike(String name) {
		return productRepository.findByProductNameLike(name);

	}

	@Override
	public Product getProductByName(String name) {
		return productRepository.findByProductNameIgnoreCase(name).orElse(null);

	}

	@Override
	public Product getProductByBarcode(String barcode) {
		return this.productRepository.findByBarcode(barcode).orElse(null);
	}

	@Override
	public Product getProductByBarcodeAndAvilability(String barcode) {
		return this.productRepository.findByBarcodeAndAvailabilityTrue(barcode).orElse(null);
	}

	@Override
	public Product saveProduct(Product product) throws GenericException {

		if (product.checkEmpty()) {
			throw new GenericException("Enter all mandatory details.");
		}

		String barcode = StringUtils.trimToEmpty(product.getBarcode());

		if (barcode.isEmpty()) { // New Product
			EAN13Barcode ean13barcode =  new EAN13Barcode();
			Long seq = sequenceGeneratorService.getNextValue("barcode");
			product.setBarcode(ean13barcode.createBarcode(seq));
		} else {
			// check bar code formatting
			if (barcode.length() != 13 || barcode.length() != 8) {
				throw new GenericException("Barcode should be of 8 or 13 digit");
			} else {
				if (barcode.length() == 13) {
					EAN13Barcode ean13barcode =  new EAN13Barcode();
					if (!ean13barcode.validate(barcode)) { // Correct Barcode
						throw new GenericException("Invalid Barcode.");
					}
				}else if (barcode.length() == 8) {
					EAN8Barcode ean8barcode = new EAN8Barcode();
					if (!ean8barcode.validate(barcode)) { // Correct Barcode
						throw new GenericException("Invalid Barcode.");
					}
				}else {
					
				}
			}
		}

		product.setProductName(product.getProductName().toUpperCase().trim());
		product.setCategory(product.getCategory().toUpperCase().trim());

		InventoryTransaction transc = new InventoryTransaction(product.getProductName(), product.getBarcode(),
				product.getUom(), AppConstant.STOCK_OPEN, product.getStockQty(), "Adding product");

		inventoryTransactionRepository.save(transc);

		return productRepository.save(product);
	}

	@Override
	public void updateAavilabilityStatus(String barcode, boolean availability) throws GenericException {
		Product product = getProductByBarcode(barcode);
		product.setAvailability(availability);
		productRepository.save(product);
	}

	@Override
	public void deleteProduct(String barcode) throws GenericException {
		Optional<Product> product = this.productRepository.findByBarcode(barcode);
		if (product.isPresent()) {
			productRepository.delete(product.get());
		} else {
			throw new GenericException("No data found for barcode: " + barcode);
		}
	}

	////////////////////////////////// Product Transaction /////////////////////
	@Override
	public void addInventoryTransactions(List<InventoryTransaction> transactions) {
		inventoryTransactionRepository.saveAll(transactions);
	}

	@Override
	public void addInventoryTransaction(InventoryTransaction transactions) {
		inventoryTransactionRepository.save(transactions);
	}

	@Override
	public void openInnventory() throws GenericException {
		boolean sts = checkInvnetoryClosingStatus();
		if (!sts)
			throw new GenericException("invnetory is already open for activity");

		List<Product> inventory = this.productRepository.findAll();
		List<InventoryTransaction> transactions = new ArrayList<>();
		inventory.forEach(inv -> {
			InventoryTransaction transc = new InventoryTransaction(inv.getProductName(), inv.getBarcode(), inv.getUom(),
					AppConstant.STOCK_OPEN, inv.getStockQty(), "Opening Product");
			transactions.add(transc);

		});

		inventoryTransactionRepository.saveAll(transactions);
		AppConstant.invStatus = false;
	}

	@Override
	public void closeInventory() throws GenericException {

		boolean sts = checkInvnetoryClosingStatus();
		if (sts)
			throw new GenericException("invnetory is already closed for activity");

		List<Product> inventory = this.productRepository.findAll();
		List<InventoryTransaction> transactions = new ArrayList<>();
		inventory.forEach(inv -> {
			InventoryTransaction transc = new InventoryTransaction(inv.getProductName(), inv.getBarcode(), inv.getUom(),
					AppConstant.STOCK_CLOSE, inv.getStockQty(), "Closing Product");
			// inventoryTransactionRepository.save(transc);
			transactions.add(transc);
		});

		// save all
		inventoryTransactionRepository.saveAll(transactions);
		AppConstant.invStatus = true;
	}

	@Override
	public boolean checkInvnetoryClosingStatus() {

		Optional<InventoryTransaction> optTransc = inventoryTransactionRepository.findFirstByOrderByActivityDtDesc();
		if (!optTransc.isPresent()) {
			return false;
		} else {
			if (optTransc.get().getActivity().equals(AppConstant.STOCK_CLOSE)) {
				return true;
			} else {
				return false;
			}
		}

	}

	@Override
	public List<InventoryTransaction> getTransactionsByDate(LocalDate startDt, LocalDate endDate) {
		LocalDateTime sd = null;
		LocalDateTime ed = null;

		if (startDt == null) {
			ed = endDate.atTime(23, 59, 59);
			return inventoryTransactionRepository.findByActivityDtLessThanEqualOrderByActivityDtAsc(ed);
		} else if (endDate == null) {
			sd = startDt.atTime(00, 00, 00);
			return inventoryTransactionRepository.findByActivityDtGreaterThanEqualOrderByActivityDtAsc(sd);
		} else {
			sd = startDt.atTime(00, 00, 00);
			ed = endDate.atTime(23, 59, 59);
			return inventoryTransactionRepository.findByActivityDtBetweenOrderByActivityDtAsc(sd, ed);

		}
	}

	@Override
	public List<InventoryTransaction> getTracsactionData(LocalDate startDate, LocalDate endDate) {
		// return
		// inventoryTransactionRepository.getTracsactionData(startDate.atTime(00,00,00),
		// endDate.atTime(23,59,59));

		return inventoryTransactionRepository.findAll();
	}

	@Override
	public List<InventoryTransaction> getTracsactionDataNative(LocalDate startDate, LocalDate endDate)
			throws GenericException {

		LocalDateTime sd = null;
		LocalDateTime ed = null;

		if (startDate == null) {
			Optional<InventoryTransaction> transc = inventoryTransactionRepository.findFirstByOrderByActivityDtAsc();
			if (!transc.isPresent()) {
				throw new GenericException("No transaction found.");
			}
			sd = transc.get().getActivityDt();
			ed = endDate.atTime(23, 59, 59);

		} else if (endDate == null) {

			Optional<InventoryTransaction> transc = inventoryTransactionRepository.findFirstByOrderByActivityDtDesc();
			if (!transc.isPresent()) {
				throw new GenericException("No transaction found.");
			}
			ed = transc.get().getActivityDt();
			sd = startDate.atTime(00, 00, 00);

		} else {
			sd = startDate.atTime(00, 00, 00);
			ed = endDate.atTime(23, 59, 59);

		}
		return inventoryTransactionRepository.getTracsactionData(sd, ed);

	}

	@Override
	public List<String> getDistinctCategory() {
		return productRepository.findDistinctCategoryOrderByCategory();

	}

	@Override
	public List<Product> getProductsByCategory(String categoryName) {
		if (StringUtils.trimToEmpty(categoryName).equals("ALL"))
			return productRepository.findAllByOrderByProductNameAsc();
		return productRepository.findByCategoryOrderByProductName(categoryName);
	}

}
