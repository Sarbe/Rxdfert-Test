package com.pos.retailer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pos.retailer.model.MasterData;

public interface MasterDataRepository extends JpaRepository<MasterData, Long> {

	public List<MasterData> findByCategory(String category);

}
