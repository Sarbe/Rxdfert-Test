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

	public List<SalesOrder> findAllByOrderByActivityDtDesc();

	@Query(value = "SELECT so.order_id FROM sales_order so "
			+ " WHERE NOT EXISTS (SELECT sod.order_detail_id FROM sales_order_details sod "
			+ " WHERE sod.order_id = so.order_id) " + " ORDER BY so.order_id asc ", nativeQuery = true)
	public String[] findEmptyOrders();

	@Query(value = "SELECT  GROUP_CONCAT(distinct so.contact_nbr SEPARATOR ', ')  AS contactNbr , "
			+ " c.party_name AS partyName, count(order_id) As orderId, "
			+ " SUM(outstanding) AS outstanding,  SUM(grand_total) As totalAmount, SUM(discount) AS discount, '"
			+ AppConstant.SALES + "' As orderType " + " FROM retailer.sales_order so, customer c "
			+ " WHERE so.party_name = c.party_name " + " AND order_sts = '" + AppConstant.ORDER_CONFIRMED + "' "
			+ " AND outstanding > 0 " + " AND c.cust_type = '" + AppConstant.CUSTOMER + "'"
			+ " GROUP BY c.party_name ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingsForAll();

	@Query(value = "SELECT  GROUP_CONCAT(distinct so.contact_nbr SEPARATOR ', ')  AS contactNbr ,"
			+ " c.party_name AS partyName, count(order_id) As orderId, "
			+ " SUM(outstanding) AS outstanding,  SUM(grand_total) As totalAmount, SUM(discount) AS discount, '"
			+ AppConstant.SALES + "' As orderType " + " FROM retailer.sales_order so, customer c "
			+ " WHERE so.party_name = c.party_name " + " AND order_sts = '" + AppConstant.ORDER_CONFIRMED + "' "
			+ " AND outstanding > 0 " + " AND c.cust_type = '" + AppConstant.CUSTOMER + "'" + " AND c.party_name = ? "
			+ " GROUP BY c.party_name ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingForOneParty(String partyName);

	@Query(value = "SELECT so.contact_nbr AS contactNbr , c.party_name AS partyName, order_id As orderId, "
			+ " outstanding AS outstanding,  grand_total As totalAmount, discount AS discount, '" + AppConstant.SALES
			+ "' As orderType " + " FROM retailer.sales_order so, customer c " + " WHERE so.party_name = c.party_name "
			+ " AND order_sts = '" + AppConstant.ORDER_CONFIRMED + "' " + " AND outstanding > 0 "
			+ " AND c.cust_type = '" + AppConstant.CUSTOMER + "'" + " AND c.party_name = ? "
			+ " ORDER BY activity_dt ASC ", nativeQuery = true)
	public List<CustomOutstanding> findDetailedOutstandingForOneParty(String partyName);

	@Query(value = "SELECT so.contact_nbr AS contactNbr , c.party_name AS partyName, order_id As orderId, "
			+ " outstanding AS outstanding,  grand_total As totalAmount, discount AS discount, '" + AppConstant.SALES
			+ "' As orderType " + " FROM retailer.sales_order so, customer c " + " WHERE so.party_name = c.party_name "
			+ " AND order_sts = '" + AppConstant.ORDER_CONFIRMED + "' " + " AND outstanding > 0 "
			+ " AND c.cust_type = '" + AppConstant.CUSTOMER + "'", nativeQuery = true)
	public List<CustomOutstanding> findDetailedOutstandingForAll();

}
