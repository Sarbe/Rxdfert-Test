package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pos.retailer.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	// public Optional<Product> findByProductId(Long productId);

	public Optional<Product> findByBarcode(String barcode);

	public Optional<Product> findByBarcodeAndProductName(String barcode, String productName);

	public Optional<Product> findByProductNameIgnoreCase(String barcode);

	public List<Product> findByProductNameLike(String name);

	@Query("SELECT productName FROM Product I ORDER BY productName ASC")
	@Cacheable("products")
	public List<String> findAllProductName();

	public Optional<Product> findByBarcodeAndAvailabilityTrue(String barcode);

	public List<Product> findByAvailability(boolean isAvail);

	@Query("select p from Product p WHERE stockQty <= thresholdQty")
	List<Product> findByThresholdQtyLessThanStockQty();

	/*@Query(nativeQuery = false, value = "select distinct(p.category) from Product p order by p.category")
	public List<String> findDistinctCategoryOrderByCategory();
	
	List<Product> findByCategoryOrderByProductName(String category);*/

	

}
