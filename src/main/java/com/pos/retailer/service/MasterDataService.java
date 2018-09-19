package com.pos.retailer.service;

import java.util.List;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.MasterData;
import com.pos.retailer.model.SellerDetails;

public interface MasterDataService {

	
	// UOM Details
	List<MasterData> getDataByCategory(String category);

	void saveMasterData(List<MasterData> datas);

	long getUOMDetailsCount();
	
	// seller
	
	SellerDetails getSellerDetails() throws GenericException;
	
	SellerDetails saveSellerDetails(SellerDetails seller);
	
	void deleteSellerDetails(Long Id);

	long getSellerDetailsCount();
}