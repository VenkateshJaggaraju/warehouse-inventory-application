package com.warehouse.inventory.WarehouseInventoryApplication.service;



import com.warehouse.inventory.WarehouseInventoryApplication.filter.JwtFilter;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Admin;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;

	private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(10);
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private JwtFilter jwtFilter;
	
	public Admin register(Admin user) {
		//encode the password before storing into db
//		the password gets stored as a BCrypt hash
		user.setPassword(encoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public String verify(Admin user) {
//		authenticated(o/p)=unauthenticated(i/p)
//		AuthenticationManager manages verification
		Authentication authentication=authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), 
																		user.getPassword()));
		if(authentication.isAuthenticated()) {
//			return "success";
			return jwtService.generateJwtToken(user.getUsername());
		}else {
			return "failed";
		}
	}

	
	// invalidating/revoking jwtToken
//	public String changePassword(String username, String newPassword) {
//		
//		Users user=userRepository.findByUsername(username);
//		
//		user.setPassword(encoder.encode(newPassword));
////		user.setTokensValidAfter(new Date());
//		userRepository.save(user);
//		
//		
//		return null;
//	}

}
