package com.pos.retailer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pos.retailer.model.SequenceGenerator;

public interface SequenceGeneratorRepository extends JpaRepository<SequenceGenerator, String> {

	Optional<SequenceGenerator> findByName(String name);

}
