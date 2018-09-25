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

	@Query(value = "SELECT distinct io.partyName,io.gstinNumber FROM InventoryOrder io ")
	public List<Object[]> findDistictVendorDetails();

	@Query(value = "SELECT io.order_id FROM inventory_order io "
			+ " WHERE NOT EXISTS (SELECT iod.order_detail_id FROM inventory_order_details iod "
			+ " WHERE iod.order_id = io.order_id) "
			+ " ORDER BY io.order_id asc", nativeQuery = true)
	public String[] findEmptyOrders();

	public List<InventoryOrder> findByOrderStsInOrderByActivityDtDesc(List<String> status);

	public List<InventoryOrder> findByOrderStsOrderByActivityDtDesc(String status);


	
	@Query(value = "SELECT  GROUP_CONCAT(io.contact_nbr SEPARATOR ', ')  AS contactNbr , c.party_name AS partyName, '' As orderId, "
			+ " SUM(outstanding) AS outstanding,  SUM(grand_total) As totalAmount, 0 AS discount, '"+AppConstant.PURCHASE+"' As orderType "
			+ " FROM retailer.inventory_order io, customer c " 
			+ " WHERE io.party_name = c.party_name "
			+ " AND order_sts = '" + AppConstant.ORDER_CONFIRMED + "' " 
			+ " AND outstanding > 0 "
			+ " AND c.cust_type = '" + AppConstant.VENDOR+"'"
			+ " GROUP BY c.party_name ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingsForAll();
	
	
	@Query(value = "SELECT  GROUP_CONCAT(io.contact_nbr SEPARATOR ', ')  AS contactNbr , c.party_name AS partyName, '' As orderId, "
			+ " SUM(outstanding) AS outstanding,  SUM(grand_total) As totalAmount, 0 AS discount, '"+AppConstant.PURCHASE+"' As orderType "
			+ " FROM retailer.inventory_order io, customer c " 
			+ " WHERE io.party_name = c.party_name "
			+ " AND order_sts = '" + AppConstant.ORDER_CONFIRMED + "' " 
			+ " AND outstanding > 0 "
			+ " AND c.cust_type = '" + AppConstant.VENDOR+"'"
			+ " AND c.party_name = ? "
			+ " GROUP BY c.party_name ", nativeQuery = true)
	public List<CustomOutstanding> findOutstandingForOneParty(String partyName);
	
	
	@Query(value = "SELECT io.contact_nbr AS contactNbr , c.party_name AS partyName, orderId As orderId, "
			+ " outstanding AS outstanding,  total_amount As totalAmount, discount AS discount, '"+AppConstant.PURCHASE+"' As orderType "
			+ " FROM retailer.inventory_order io, customer c " 
			+ " WHERE io.party_name = c.party_name "
			+ " AND order_sts = '" + AppConstant.ORDER_CONFIRMED + "' " 
			+ " AND outstanding > 0 "
			+ " AND c.cust_type = '" + AppConstant.VENDOR+"'"
			+ " AND c.party_name = ? ", nativeQuery = true)
	public List<CustomOutstanding> findDetailedOutstandingForOneParty(String partyName);
	
}
