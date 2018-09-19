package com.pos.retailer.service;

import java.util.List;

import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.AppUser;

public interface AppUserService {
	AppUser save(AppUser user);
	List<AppUser> getAllUsers();
	void update(Long userid, AppUser user) throws GenericException;
	void changeUserStaus(String loogedInUser, String userName) throws GenericException;
	void updatePassword(Long userid, AppUser user) throws GenericException;
}