package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pos.retailer.model.CustomerDetails;

public interface CustomerDetailsRepository extends JpaRepository<CustomerDetails, Long> {

	public Optional<CustomerDetails> findByContactNbrAndCustomerType(String contactNbr, String custType);
	
	public List<CustomerDetails> findByCustomerType(String custType);

	@Query("Select distinct c from CustomerDetails c WHERE c.customerType = ?1 ORDER BY c.partyName ASC")
	public List<CustomerDetails> findDistinctDetails(String custType);

	
	
	/*@Query(nativeQuery = true, value = "select c.contact_nbr AS contactNbr, c.party_name AS partyName, c.gstin AS gstin, "
			+ " c.address AS address, ifnull( sum(so.outstanding),0) AS outstanding from "
			+ " (select * from customer where cust_type = '" + AppConstant.CUSTOMER +"'  ) c LEFT JOIN sales_order so "
			+ " ON c.contact_nbr = so.contact_nbr "
			+ " group by c.contact_nbr ")
	public List<CustomPartyDetails> findGroupedCustDetails();

	@Query(nativeQuery = true, value = "select c.contact_nbr AS contactNbr, c.party_name AS partyName, c.gstin AS gstin, "
			+ " c.address AS address, ifnull( sum(io.outstanding),0) AS outstanding " + " from "
			+ " (select * from customer where cust_type = '" + AppConstant.VENDOR +"'  ) c LEFT JOIN inventory_order io "
			+ " ON c.contact_nbr = io.contact_nbr "
			+ " group by c.contact_nbr ")
	public List<CustomPartyDetails> findGroupedVendorDetails();*/

	
	/*@Query(nativeQuery = true, value = "select c.contact_nbr AS contactNbr, c.party_name AS partyName, c.gstin AS gstin, "
			+ " c.address AS address, sum(so.outstanding) AS outstanding from customer c, sales_order so "
			+ " where c.contact_nbr = so.contact_nbr "
			+ " and c.contact_nbr = ? "
			+ " and c.cust_type = '" + AppConstant.CUSTOMER + "' "
			+ " group by c.contact_nbr ")
	public List<CustomPartyDetails> findSalesOrdersBycontactNbr(String contactNbr);

	@Query(nativeQuery = true, value = "select c.contact_nbr AS contactNbr, c.party_name AS partyName, c.gstin AS gstin, "
			+ " c.address AS address, sum(io.outstanding) AS outstanding " + " from customer c, inventory_order io "
			+ " where c.contact_nbr = io.contact_nbr "
			+ " and c.contact_nbr = ? "
			+ " and c.cust_type = '" + AppConstant.VENDOR + "' "
			+ " group by c.contact_nbr ")
	public List<CustomPartyDetails> findInvOrdersBycontactNbr(String contactNbr);*/

	
}
