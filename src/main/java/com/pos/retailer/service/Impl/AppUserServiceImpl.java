package com.pos.retailer.service.Impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.pos.retailer.component.AppConstant;
import com.pos.retailer.exception.GenericException;
import com.pos.retailer.model.AppUser;
import com.pos.retailer.repository.AppUserRepository;
import com.pos.retailer.service.AppUserService;

@Service
public class AppUserServiceImpl implements UserDetailsService, AppUserService {

	@Autowired
	AppUserRepository usersRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		AppUser user = usersRepository.findByActiveTrueAndUsernameIgnoreCase(username);
		System.out.println(user);
		if (user == null) {
			throw new UsernameNotFoundException(username + " was not found");
		}
		return new User(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRoles()));
	}

	@Override
	public AppUser save(AppUser user) {
		Optional<AppUser> optDbUser = usersRepository.findByUsername(user.getUsername());
		if (optDbUser.isPresent()) { // existing
			if (user.isChangePassword())
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		} else { // new
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setActive(true);
		}

		return usersRepository.save(user);
	}

	@Override
	public List<AppUser> getAllUsers() {
		return usersRepository
				.findByRolesInOrderByUsername(Arrays.asList(AppConstant.ROLE_ADMIN, AppConstant.ROLE_CLERK));

	}

//	@Override
//	public AppUser update(Long userId, AppUser user) throws GenericException {
//		Optional<AppUser> dbOptUser = usersRepository.findById(userId);
//		if (!dbOptUser.isPresent()) {
//			throw new GenericException("User details not present");
//		}
//
//		AppUser dbUser = dbOptUser.get();
//		dbUser.setFirstName(user.getFirstName());
//		dbUser.setFirstName(user.getLastName());
//		dbUser.setFirstName(user.getUsername());
//		dbUser.setActive(user.isActive());
//		if (user.isChangePassword())
//			dbUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
//
//		return usersRepository.save(dbUser);
//	}

	@Override
	public void changeUserStaus(String loogedInUser, String userName) throws GenericException {
		AppUser dbUser = usersRepository.findByUsernameIgnoreCase(userName);
		if (ObjectUtils.isEmpty(dbUser)) {
			throw new GenericException("User details not present");
		}

		if (dbUser.getUsername().equalsIgnoreCase(loogedInUser)) {
			throw new GenericException("Can not de-activate own account");
		}
		dbUser.setActive(!dbUser.isActive());

		usersRepository.save(dbUser);
	}

	/*@Override
	public void updatePassword(Long userId, AppUser user) throws GenericException {
		Optional<AppUser> dbOptUser = usersRepository.findById(userId);
		if (!dbOptUser.isPresent()) {
			throw new GenericException("User details not present");
		}

		AppUser dbUser = dbOptUser.get();
		dbUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		usersRepository.save(dbUser);

	}*/
}