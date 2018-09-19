package com.pos.retailer.service;

import java.util.List;

import com.pos.retailer.model.CustomerDetails;

public interface CustomerService {

	//CustomerDetails getCustomerByContactNumber(String contactNbr);

	CustomerDetails saveCustomer(CustomerDetails customer);

	List<CustomerDetails> getUniqueDetails(String custType);

	CustomerDetails getCustomerByContactNumber(String contactNbr, String custType);

	List<CustomerDetails> getAllPartyDetailsOfCustType(String custType);


}