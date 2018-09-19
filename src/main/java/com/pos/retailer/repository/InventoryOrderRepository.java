package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.model.InventoryOrder;
import com.pos.retailer.repository.model.CustomOutstanding;

public interface InventoryOrderRepository extends JpaRepository<InventoryOrder, String> {

	public Optional<InventoryOrder> findByOrderId(String id);

	public Optional<InventoryOrder> findByOrderIdAndOrderSts(String id, String sts);

	public List<InventoryOrder> findAllByOrderByOrderIdDesc();

	public List<InventoryOrder> findByBillNbr(String receiptNbr);

	public List<InventoryOrder> findByPartyName(String partyName);

	public List<InventoryOrder> findByGstinNumber(String partyName);

	public String findOrderStsByOrderId(String Id);

	public List<InventoryOrder> findBySettled(boolean isSettled);

	@Query(value = "select distinct io.partyName,io.gstinNumber from InventoryOrder io")
	public List<Object[]> findDistictVendorDetails();

	@Query(value = "select io.order_id from inventory_order io "
			+ " where not exists (select iod.order_detail_id from inventory_order_details iod where iod.order_id = io.order_id) "
			+ " order by io.order_id asc", nativeQuery = true)
	public String[] findEmptyOrders();

	@Query(value = "SELECT io.contact_nbr AS contactNbr , c.party_name AS partyName, "
			+ " io.order_id AS orderId, grand_total AS totalAmount,0 AS discount, outstanding AS outstanding "
			+ " FROM retailer.inventory_order io, customer c "
			+ " where io.contact_nbr = c.contact_nbr and order_sts = '" + AppConstant.ORDER_CONFIRMED + "' "
			+ " and outstanding > 0 and c.cust_type = '" + AppConstant.VENDOR
			+ "' ORDER BY c.party_name ASC ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandings();

	public List<InventoryOrder> findByOrderStsInOrderByActivityDtDesc(List<String> status);

	public List<InventoryOrder> findByOrderStsOrderByActivityDtDesc(String status);

	//////////////////
	// search
	@Query(value = "SELECT io.contact_nbr AS contactNbr , c.party_name AS partyName, "
			+ " sum(outstanding) AS outstanding, '' AS orderId,  0 As totalAmount, 0 AS discount "
			+ " FROM retailer.inventory_order io, customer c " + " where io.contact_nbr = c.contact_nbr and order_sts = '"
			+ AppConstant.ORDER_CONFIRMED + "' " + " and outstanding > 0 and c.cust_type = '" + AppConstant.CUSTOMER
			+ "' and c.party_name = ? ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingsByPartyName(String contactNbr);

	@Query(value = "SELECT io.contact_nbr AS contactNbr , c.party_name AS partyName, "
			+ " sum(outstanding) AS outstanding ,'' AS orderId,  0 As totalAmount, 0 AS discount "
			+ " FROM retailer.inventory_order io, customer c " + " where io.contact_nbr = c.contact_nbr and order_sts = '"
			+ AppConstant.ORDER_CONFIRMED + "' " + " and outstanding > 0 and c.cust_type = '" + AppConstant.CUSTOMER
			+ "' and io.contact_nbr = ? GROUP BY io.contact_nbr ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingsByContactNbr(String contactNbr);

	// detailed Order
	////////////
	@Query(value = "SELECT io.order_id, io.outstanding AS outstanding,io.contact_nbr AS contactNbr, "
			+ " c.party_name AS partyName,  0 As totalAmount, 0 AS discount  "
			+ " FROM retailer.inventory_order io, customer c " + " where io.contact_nbr = c.contact_nbr and order_sts = '"
			+ AppConstant.ORDER_CONFIRMED + "' " + " and outstanding > 0 and c.cust_type = '" + AppConstant.CUSTOMER
			+ "' and c.party_name = ? ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingOrdersByPartyName(String partyName);

	@Query(value = "SELECT io.order_id, io.outstanding AS outstanding,io.contact_nbr AS contactNbr,"
			+ " c.party_name AS partyName, 0 As totalAmount, 0 AS discount   "
			+ " FROM retailer.inventory_order io, customer c " + " where io.contact_nbr = c.contact_nbr and order_sts = '"
			+ AppConstant.ORDER_CONFIRMED + "' " + " and outstanding > 0 and c.cust_type = '" + AppConstant.CUSTOMER
			+ "' and io.contact_nbr = ? ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingOrdersByContactNbr(String contactNbr);

}
