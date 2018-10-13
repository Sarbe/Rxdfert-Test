package com.pos.retailer.service.Impl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

	// @Override
	// public ProductSummary getProductSummary() {
	// return productRepository.findAllProductSummary();
	// }

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

		if (barcode.isEmpty()) { // New Product or No custom product with no barcode
			EAN13Barcode ean13barcode = new EAN13Barcode();
			Long seq = sequenceGeneratorService.getNextValue("barcode");
			product.setBarcode(ean13barcode.createBarcode(seq));
		} else {
			// check bar code formatting
			if (barcode.length() != 13 && barcode.length() != 8) {
				throw new GenericException("Barcode should be of 8 or 13 digit");
			} else {
				if (barcode.length() == 13) {
					EAN13Barcode ean13barcode = new EAN13Barcode();
					if (!ean13barcode.validate(barcode)) { // Correct Barcode
						throw new GenericException("Invalid Barcode.");
					}
				} else if (barcode.length() == 8) {
					EAN8Barcode ean8barcode = new EAN8Barcode();
					if (!ean8barcode.validate(barcode)) { // Correct Barcode
						throw new GenericException("Invalid Barcode.");
					}
				} else {

				}
			}
		}

		product.setProductName(product.getProductName().toUpperCase().trim());
		product.setCategory(product.getCategory().toUpperCase().trim());
		
		// Add to transaction history for the first time add profuct not for the update
		Optional<Product> dbOptProduct = productRepository.findByBarcode(product.getBarcode());
		if (!dbOptProduct.isPresent()) {
			InventoryTransaction transc = new InventoryTransaction(product.getProductName(), product.getBarcode(),
					product.getUom(), AppConstant.STOCK_OPEN, 0, "Adding product");
			inventoryTransactionRepository.save(transc);

			if (product.getStockQty() > 0) {
				transc = new InventoryTransaction(product.getProductName(), product.getBarcode(), product.getUom(),
						AppConstant.STOCK_IN, product.getStockQty(), "Initial Quantity");

				inventoryTransactionRepository.save(transc);
			}
		}

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

	// read and persist from xlxs file
	@Override
	public List<Product> processFileData(InputStream is) throws GenericException {
		CopyOnWriteArrayList<Product> products = new CopyOnWriteArrayList<>();

		XSSFWorkbook workbook = null;
		try {
			workbook = new XSSFWorkbook(is);
			XSSFSheet sheet = workbook.getSheetAt(0);

			Iterator<Row> rows = sheet.rowIterator();
			XSSFRow row;

			int noOfRows = sheet.getPhysicalNumberOfRows();
			if (noOfRows < 1)
				throw new GenericException("File seems to be blank. Please upload a correct file");

			Product prd = null;
			while (rows.hasNext()) {
				row = (XSSFRow) rows.next();
				if (row.getRowNum() == 0) // header
					continue;
				else {
					prd = new Product();
					prd.setProductName(row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "");
					prd.setMaxRetailPrice(row.getCell(1) != null ? row.getCell(1).getNumericCellValue() : 0);
					prd.setBuyPrice(row.getCell(2) != null ? row.getCell(2).getNumericCellValue() : 0);
					prd.setSellPrice(row.getCell(3) != null ? row.getCell(3).getNumericCellValue() : 0);
					prd.setCategory(row.getCell(4) != null ? row.getCell(4).getStringCellValue() : "");
					prd.setManufacturer(row.getCell(5) != null ? row.getCell(5).getStringCellValue() : "");
					prd.setUom(row.getCell(6) != null ? row.getCell(6).getStringCellValue() : "");

					products.add(prd);
				}
				System.out.println("Size >"+products.size());

			}
		} catch (Exception e) {
			throw new GenericException(e.getMessage());
		} finally {
			if (null != workbook) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		// add to database

		/*for (Product product : products) {
			try {
				if (product.checkEmpty())
					throw new Exception("All mandatory data is not present.");
				saveProduct(product);
			} catch (Exception e) {
				product.setErrMsg(e.getMessage());
				failedProduct.add(product);
			}
		}

		return failedProduct;
*/
		
		return products;
	}

	@Override
	public byte[] writeToXLSXFile(List<Product> products) throws GenericException {

		String sheetName = "failedData";// name of sheet
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		
		try {
			// iterating r number of rows
			XSSFRow row = null;
			for (int i = 0; i < products.size(); i++) {
				Product prd = products.get(i);
				row = sheet.createRow(i);

				row.createCell(0).setCellValue(prd.getProductName());
				row.createCell(1).setCellValue(prd.getMaxRetailPrice());
				row.createCell(2).setCellValue(prd.getBuyPrice());
				row.createCell(3).setCellValue(prd.getSellPrice());
				row.createCell(4).setCellValue(prd.getCategory());
				row.createCell(5).setCellValue(prd.getManufacturer());
				row.createCell(6).setCellValue(prd.getUom());
				row.createCell(7).setCellValue(prd.getErrMsg());

			}
			wb.write(baos);
			
			
//			FileOutputStream fileOut = new FileOutputStream("D:/aabb.xlsx");
//
//			//write this workbook to an Outputstream.
//			wb.write(fileOut);
//			fileOut.flush();
//			fileOut.close();
			
		} catch (Exception e) {
			throw new GenericException("Error occured while cretaing the file");
		} finally {
			try {
				wb.close();
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return baos.toByteArray();
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, GenericException {
		String fileName = "abc.xlxs";
		System.out.println(fileName.substring(fileName.lastIndexOf(".") + 1));

	}

}
