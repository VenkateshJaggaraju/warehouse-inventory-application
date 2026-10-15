package com.warehouse.inventory.WarehouseInventoryApplication.service;

import Spring_Security.Spring_Security.entity.UserPrincipal;
import Spring_Security.Spring_Security.entity.Users;
import Spring_Security.Spring_Security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users user = userRepository.findByUsername(username);

		if (user == null) {
			System.out.println("User Not Found");
			throw new UsernameNotFoundException("user not found");
		}
		System.out.println("Username "+user.getUsername());
		System.out.println("Password "+user.getPassword());

		return new UserPrincipal(user);
	}

}
