package com.pos.retailer.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pos.retailer.model.CustomerDetails;
import com.pos.retailer.repository.CustomerDetailsRepository;
import com.pos.retailer.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerDetailsRepository customerDetailRepository;

	
	@Override
	public List<CustomerDetails> getAllPartyDetailsOfCustType(String custType){
		return customerDetailRepository.findByCustomerType(custType);
	}
	
	@Override
	public CustomerDetails getCustomerByContactNumber(String contactNbr, String custType) {
		return customerDetailRepository.findByContactNbrAndCustomerType(contactNbr, custType).orElse(null);
	}

	@Override
	public CustomerDetails saveCustomer(CustomerDetails customer) {
		return customerDetailRepository.save(customer);
	}

	@Override
	public List<CustomerDetails> getUniqueDetails(String custType) {
		return customerDetailRepository.findDistinctDetails(custType);
	}


}
