package com.pos.retailer.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pos.retailer.model.InventoryTransaction;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

	public List<InventoryTransaction> findByProductName(String productName);

	public Optional<InventoryTransaction> findLastByOrderByActivityDtDesc();

	public List<InventoryTransaction> findByActivityDtBetweenOrderByActivityDtAsc(LocalDateTime startDt,
			LocalDateTime endDt);

	public List<InventoryTransaction> findByActivityDtGreaterThanEqualOrderByActivityDtAsc(LocalDateTime startDt);

	public List<InventoryTransaction> findByActivityDtLessThanEqualOrderByActivityDtAsc(LocalDateTime endDt);

	public Optional<InventoryTransaction> findFirstByOrderByActivityDtDesc();

	public Optional<InventoryTransaction> findFirstByOrderByActivityDtAsc();

	@Query(value = "SELECT i from InventoryTransaction i " + " where i.activityDt > ?1  " + " and i.activityDt < ?2 ")
	List<InventoryTransaction> getTracsactionData(LocalDateTime start, LocalDateTime end);
}
