package com.warehouse.inventory.WarehouseInventoryApplication.service;

import com.warehouse.inventory.WarehouseInventoryApplication.enums.Role;
import com.warehouse.inventory.WarehouseInventoryApplication.model.Admin;
import com.warehouse.inventory.WarehouseInventoryApplication.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    public Admin register(Admin user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        // Public self-registration must never grant privileges:
        // the very first user becomes ADMIN, everybody else is WAREHOUSE_STAFF.
        user.setRole(userRepository.count() == 0 ? Role.ADMIN : Role.WAREHOUSE_STAFF);
        return userRepository.save(user);
    }

    public Admin changeRole(String username, Role role) {
        Admin user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username);
        }
        user.setRole(role);
        return userRepository.save(user);
    }

    public String verify(String username, String password) {
        // throws AuthenticationException (-> 401) when credentials are wrong
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return jwtService.generateJwtToken(username);
    }

    public String logout(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing bearer token");
        }
        String token = header.substring(7);
        tokenBlacklistService.blacklistToken(token, jwtService.extractExpiration(token));
        return "Logged out successfully";
    }
}
