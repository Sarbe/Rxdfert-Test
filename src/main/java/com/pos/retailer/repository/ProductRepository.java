package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pos.retailer.model.Product;
import com.pos.retailer.repository.model.ProductSummary;

public interface ProductRepository extends JpaRepository<Product, Long> {

	
	public Optional<Product> findByProductId(Long productId);

	public Optional<Product> findByBarcode(String barcode);

	public Optional<Product> findByBarcodeAndProductName(String barcode, String productName);

	public Optional<Product> findByProductNameIgnoreCase(String barcode);

	public List<Product> findByProductNameLike(String name);

	@Query("SELECT new com.pos.retailer.repository.model.ProductSummary(count(barcode), SUM(buyPrice)) FROM Product ")
	//@Query("SELECT count(barcode) as productCount, SUM(buyPrice) as stockValue FROM Product ")
	@Cacheable("productSummary")
	public ProductSummary findAllProductSummary();
	
	@Query("SELECT productName FROM Product I ORDER BY productName ASC")
	@Cacheable("products")
	public List<String> findAllProductName();
	
	@Query("SELECT productName FROM Product P WHERE category = ?1 ORDER BY productName ASC")
	@Cacheable("products")
	public List<String> findAllProductNameByCatrgory(String category);
	
	public List<Product> findAllByOrderByProductNameAsc();

	public Optional<Product> findByBarcodeAndAvailabilityTrue(String barcode);

	public List<Product> findByAvailability(boolean isAvail);

	@Query("select p from Product p WHERE stockQty <= thresholdQty")
	List<Product> findByThresholdQtyLessThanStockQty();

	@Query(nativeQuery = false, value = "select distinct(p.category) from Product p where category is not null order by p.category Asc")
	public List<String> findDistinctCategoryOrderByCategory();
	
	List<Product> findByCategoryOrderByProductName(String category);

	@Query("select distinct(manufacturer) from Product p where manufacturer is not null order by manufacturer Asc")
	public List<String> findDistinctManufacturer();
	

}
