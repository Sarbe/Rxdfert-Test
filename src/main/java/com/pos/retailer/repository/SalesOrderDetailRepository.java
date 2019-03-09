package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pos.retailer.model.SalesOrderDetails;
import com.pos.retailer.repository.model.ProductOverview;

public interface SalesOrderDetailRepository extends JpaRepository<SalesOrderDetails, Long> {

	public Optional<SalesOrderDetails> findByOrderDetailId(Long id);

	public List<SalesOrderDetails> findByOrderId(String orderId);

	public SalesOrderDetails findByOrderIdAndBarcode(String orderId, String barcode);

	public void deleteAllByOrderId(String orderId);
	
	
	/*@Query("select p from Product p where productName like %?1% OR barcode like %?1% "
			+ " ORDER BY productName ASC")
	public List<Product> findByProductNameLikeSearchCriteria(String searchCriteria);*/
	
	@Query("SELECT DATE_FORMAT(CREATION_DATE,'%b %Y') AS cdate,COUNT(9) AS count FROM  SalesOrderDetails "
			+ " WHERE barcode = ?1 GROUP BY DATE_FORMAT(CREATION_DATE,'%b %Y') ORDER BY CREATION_DATE ASC ")
	public List<ProductOverview> productSaleOverview(String barcode);

}
