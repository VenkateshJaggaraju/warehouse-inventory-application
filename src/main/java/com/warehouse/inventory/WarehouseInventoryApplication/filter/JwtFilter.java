
package com.warehouse.inventory.WarehouseInventoryApplication.filter;




import com.warehouse.inventory.WarehouseInventoryApplication.service.JwtService;
import com.warehouse.inventory.WarehouseInventoryApplication.service.MyUserDetailsService;
import com.warehouse.inventory.WarehouseInventoryApplication.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // ---------------------------------------------
        // Get JWT from Authorization header
        // ---------------------------------------------

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);

            // ---------------------------------------------
            // Check whether JWT was invalidated during logout
            // ---------------------------------------------

            if (tokenBlacklistService.isBlacklisted(token)) {

                // Token was logged out.
                // Do not authenticate this request.
                filterChain.doFilter(request, response);
                return;
            }

            // ---------------------------------------------
            // Extract username from JWT
            // ---------------------------------------------

            try {

                username = jwtService.extractUsername(token);

            } catch (Exception e) {

                // Invalid / expired / malformed JWT
                username = null;
            }
        }

        // ---------------------------------------------
        // Authenticate user if JWT is valid
        // ---------------------------------------------

        if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {

                UserDetails userDetails =
                        context.getBean(MyUserDetailsService.class)
                                .loadUserByUsername(username);

                // ---------------------------------------------
                // Validate JWT
                // ---------------------------------------------

                if (jwtService.validateToken(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);
                }

            } catch (Exception e) {

                // Invalid JWT or user not found.
                // Leave request unauthenticated.
                SecurityContextHolder.clearContext();
            }
        }

        // ---------------------------------------------
        // Continue request
        // ---------------------------------------------

        filterChain.doFilter(request, response);
    }
}