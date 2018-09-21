package com.pos.retailer.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pos.retailer.component.ResponseWrapper;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.AppUser;
import com.pos.retailer.service.AppUserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private AppUserService appUserService;

	@GetMapping
	public ResponseEntity<?> getAllUserDetails() {
		return new ResponseWrapper<>("All User Details", HttpStatus.OK, appUserService.getAllUsers()).sendResponse();
	}

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@RequestBody AppUser user) {
		
		return new ResponseWrapper<>("User Details Saved", HttpStatus.OK, appUserService.save(user)).sendResponse();
	}

	/*@PutMapping("/{userid}")
	public ResponseEntity<?> updateUser(@PathVariable Long userid, @RequestBody AppUser user) throws GenericException {

		appUserService.update(userid, user);
		return new ResponseWrapper<>("User Details Saved", HttpStatus.OK, null).sendResponse();
	}*/

	/*@PostMapping("/{userId}/updatePassword")
	public ResponseEntity<?> updatePassword(@PathVariable Long userId, @RequestBody AppUser user) throws GenericException {

		appUserService.updatePassword(userId, user);
		return new ResponseWrapper<>("Password updated", HttpStatus.OK, null).sendResponse();
	}*/

	
	@DeleteMapping("/{userName}")
	public ResponseEntity<?> deleteByUserName(Principal p, @PathVariable String userName) throws GenericException {

		appUserService.changeUserStaus(p.getName(), userName);
		return new ResponseWrapper<>("User Deleted", HttpStatus.OK, null).sendResponse();
	}
	
	

}
