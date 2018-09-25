package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pos.retailer.model.CustomerDetails;

public interface CustomerDetailsRepository extends JpaRepository<CustomerDetails, Long> {

	public Optional<CustomerDetails> findByContactNbrAndCustomerType(String contactNbr, String custType);
	
	public List<CustomerDetails> findByCustomerTypeOrderByPartyName(String custType);

	@Query("Select distinct c from CustomerDetails c WHERE c.customerType = ?1 ORDER BY c.partyName ASC")
	public List<CustomerDetails> findDistinctDetails(String custType);

	/*@Query(value = "SELECT  GROUP_CONCAT(io.contact_nbr SEPARATOR ', ')  AS contactNbr , c.party_name AS partyName, '' As orderId, "
			+ " SUM(outstanding) AS outstanding,  SUM(total_amount) As totalAmount, SUM(discount) AS discount "
			+ " FROM retailer.inventory_order io, customer c " 
			+ " WHERE io.party_name = c.party_name "
			+ " AND order_sts = '" + AppConstant.ORDER_CONFIRMED + "' " 
			+ " AND outstanding > 0 "
			+ " AND c.cust_type = ? "
			+ " GROUP BY c.party_name ", nativeQuery = true)
	public List<CustomOutstanding> findTotalOutstandingDetails(String custType);
	
	
	@Query(value = "SELECT  GROUP_CONCAT(io.contact_nbr SEPARATOR ', ')  AS contactNbr , c.party_name AS partyName, '' As orderId, "
			+ " SUM(outstanding) AS outstanding,  SUM(total_amount) As totalAmount, SUM(discount) AS discount "
			+ " FROM retailer.inventory_order io, customer c " 
			+ " WHERE io.party_name = c.party_name "
			+ " AND order_sts = '" + AppConstant.ORDER_CONFIRMED + "' " 
			+ " AND outstanding > 0 "
			+ " AND c.cust_type = ? "
			+ " AND c.party_name = ? "
			+ " GROUP BY c.party_name ", nativeQuery = true)
	public List<CustomOutstanding> findTotalOutstandingByPartyName(String custType, String partyName);
	
	
	@Query(value = "SELECT io.contact_nbr AS contactNbr , c.party_name AS partyName, orderId As orderId, "
			+ " outstanding AS outstanding,  total_amount As totalAmount, discount AS discount "
			+ " FROM retailer.inventory_order io, customer c " 
			+ " WHERE io.party_name = c.party_name "
			+ " AND order_sts = '" + AppConstant.ORDER_CONFIRMED + "' " 
			+ " AND outstanding > 0 "
			+ " AND c.cust_type = ? "
			+ " AND c.party_name = ? ", nativeQuery = true)
	public List<CustomOutstanding> findDetailedOutstandingByPartyName(String custType, String partyName);*/
	
}
