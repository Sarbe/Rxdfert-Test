package com.pos.retailer.report;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pos.retailer.service.ProductService;

import net.sf.jasperreports.engine.JRException;

@Component
public class InventoryReport extends ReportUtility {
	@Autowired
	ProductService inventoryService;

	@Override
	public void reportConstructor(Object... inputData) throws Exception {

		//LocalDate startDate = (LocalDate) inputData[0];
		//LocalDate endDate = (LocalDate) inputData[1];

		//List<InventoryTransaction> transactions = productService.getTransactionsByDate(startDate, endDate);
	}

	@Override
	public void reportMetadata(String reportParams) throws JRException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillReport() throws JRException {
		// TODO Auto-generated method stub

	}
	
	
}
