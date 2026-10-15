package com.warehouse.inventory.WarehouseInventoryApplication.controller;

import Spring_Security.Spring_Security.Service.UserService;
import Spring_Security.Spring_Security.entity.Users;
import Spring_Security.Spring_Security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsersController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/getUsers")
	public List<Users> getUsers() {
		return userRepository.findAll();
	}
	
	@PostMapping("/register")
	public Users register(@RequestBody Users user) {
		return userService.register(user);
	}
	
	@PostMapping("/login")
	public String login(@RequestBody Users user) {
//		return "success";
		return userService.verify(user);
	}
	
	
//	@PostMapping("/updatePassword")
//	public String changePassword(@RequestParam String username, @RequestParam String newPassword) {
//		return userService.changePassword(username, newPassword);
//	}
	
}
