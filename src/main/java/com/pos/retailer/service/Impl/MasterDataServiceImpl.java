package com.pos.retailer.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.MasterData;
import com.pos.retailer.model.SellerDetails;
import com.pos.retailer.repository.MasterDataRepository;
import com.pos.retailer.repository.SellerDetailsRepository;
import com.pos.retailer.service.MasterDataService;

@Service
public class MasterDataServiceImpl implements MasterDataService {

	@Autowired
	private MasterDataRepository masterDataRepository;

	@Autowired
	private SellerDetailsRepository sellerDetailsRepository;
	
	
	@Override
	public List<MasterData> getDataByCategory(String category) {
		return masterDataRepository.findByCategory(category);
	}

	@Override
	public void saveMasterData(List<MasterData> datas) {
		masterDataRepository.saveAll(datas);
		
	}
	
	@Override
	public long getUOMDetailsCount(){
		return masterDataRepository.count();
	}

	//////////////////////////////////////

	@Override
	public SellerDetails getSellerDetails() throws GenericException {
		List<SellerDetails> sdList = sellerDetailsRepository.findAll();
		if(sdList == null || sdList.isEmpty()) {
			throw new GenericException("Seller Details not present.");
		}
		return sdList.get(0);
	}

	@Override
	public SellerDetails saveSellerDetails(SellerDetails seller) {
		return sellerDetailsRepository.save(seller);
	}

	@Override
	public void deleteSellerDetails(Long Id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getSellerDetailsCount() {
		return sellerDetailsRepository.count();
		
	}
	
	
}
