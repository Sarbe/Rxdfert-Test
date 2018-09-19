package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.model.SalesOrder;
import com.pos.retailer.repository.model.CustomOutstanding;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, String> {

	public Optional<SalesOrder> findByOrderId(String id);

	public List<SalesOrder> findByOrderStsInOrderByActivityDtDesc(List<String> status);

	public List<SalesOrder> findByOrderStsOrderByActivityDtDesc(String status);

	public List<SalesOrder> findBySettled(boolean isSettled);

	public List<SalesOrder> findAllByOrderByOrderIdDesc();

	@Query(value = "select so.order_id from sales_order so "
			+ " where not exists (select sod.order_detail_id from sales_order_details sod where sod.order_id = so.order_id) "
			+ " order by so.order_id asc ", nativeQuery = true)
	public String[] findEmptyOrders();

	// @Query(value = "SELECT so.contact_nbr AS contactNbr , c.party_name AS
	// partyName, sum(grand_total) AS totalAmount,sum(outstanding) AS outstanding "
	// + " FROM retailer.sales_order so, customer c "
	// + " where so.contact_nbr = c.contact_nbr and order_sts = 'CONFIRMED' "
	// + " and outstanding > 0 "
	// + " group by so.contact_nbr having SUM(outstanding) > 0 ", nativeQuery =
	// true)
	// public List<CustomOutstanding> findOutstandings();

	@Query(value = "SELECT so.contact_nbr AS contactNbr , c.party_name AS partyName, so.order_id AS orderId, "
			+ " grand_total AS totalAmount,discount AS discount, outstanding AS outstanding "
			+ " FROM retailer.sales_order so, customer c " + " where so.contact_nbr = c.contact_nbr and order_sts = '"
			+ AppConstant.ORDER_CONFIRMED + "' " + " and outstanding > 0 and c.cust_type = '" + AppConstant.CUSTOMER
			+ "'  ORDER BY c.party_name ASC ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandings();

	//////////////////
	// search
	@Query(value = "SELECT so.contact_nbr AS contactNbr , c.party_name AS partyName, "
			+ " sum(outstanding) AS outstanding, '' AS orderId,  0 As totalAmount, 0 AS discount "
			+ " FROM retailer.sales_order so, customer c " + " where so.contact_nbr = c.contact_nbr and order_sts = '"
			+ AppConstant.ORDER_CONFIRMED + "' " + " and outstanding > 0 and c.cust_type = '" + AppConstant.CUSTOMER
			+ "' and c.party_name = ? ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingsByPartyName(String contactNbr);

	@Query(value = "SELECT so.contact_nbr AS contactNbr , c.party_name AS partyName, "
			+ " sum(outstanding) AS outstanding ,'' AS orderId,  0 As totalAmount, 0 AS discount "
			+ " FROM retailer.sales_order so, customer c " + " where so.contact_nbr = c.contact_nbr and order_sts = '"
			+ AppConstant.ORDER_CONFIRMED + "' " + " and outstanding > 0 and c.cust_type = '" + AppConstant.CUSTOMER
			+ "' and so.contact_nbr = ? GROUP BY so.contact_nbr ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingsByContactNbr(String contactNbr);

	// detailed Order
	////////////
	@Query(value = "SELECT so.order_id, so.outstanding AS outstanding,so.contact_nbr AS contactNbr, "
			+ " c.party_name AS partyName,  0 As totalAmount, 0 AS discount  "
			+ " FROM retailer.sales_order so, customer c " + " where so.contact_nbr = c.contact_nbr and order_sts = '"
			+ AppConstant.ORDER_CONFIRMED + "' " + " and outstanding > 0 and c.cust_type = '" + AppConstant.CUSTOMER
			+ "' and c.party_name = ? ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingOrdersByPartyName(String partyName);

	@Query(value = "SELECT so.order_id, so.outstanding AS outstanding,so.contact_nbr AS contactNbr,"
			+ " c.party_name AS partyName, 0 As totalAmount, 0 AS discount   "
			+ " FROM retailer.sales_order so, customer c " + " where so.contact_nbr = c.contact_nbr and order_sts = '"
			+ AppConstant.ORDER_CONFIRMED + "' " + " and outstanding > 0 and c.cust_type = '" + AppConstant.CUSTOMER
			+ "' and so.contact_nbr = ? ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingOrdersByContactNbr(String contactNbr);

}
