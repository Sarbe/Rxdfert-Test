package com.pos.retailer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pos.retailer.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	AppUser findByActiveTrueAndUsernameIgnoreCase(String username);
	
	AppUser findByUsernameIgnoreCase(String userName);

	List<AppUser> findByRolesIn(List<String> roles);

	Optional<AppUser> findByUsername(String userName);

	void deleteByUsername(String userId);

	

}