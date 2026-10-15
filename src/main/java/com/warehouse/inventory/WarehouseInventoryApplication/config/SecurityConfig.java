package com.warehouse.inventory.WarehouseInventoryApplication.config;

import com.warehouse.inventory.WarehouseInventoryApplication.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // Authentication endpoints
                        .requestMatchers("/register", "/login").permitAll()

                        // Swagger and OpenAPI documentation
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Product, warehouse, supplier management
                        .requestMatchers("/api/products/**",
                                "/api/warehouses/**",
                                "/api/suppliers/**")
                        .hasRole("ADMIN")

                        // Reconciliation and stock receipts/transfers
                        .requestMatchers(
                                "/api/inventory/receipts",
                                "/api/inventory/transfers",
                                "/api/reconciliations",
                                "/api/reconciliations/**"
                        ).hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")

                        // Dispatch operations
                        .requestMatchers("/api/inventory/dispatches")
                        .hasAnyRole(
                                "ADMIN",
                                "WAREHOUSE_MANAGER",
                                "WAREHOUSE_STAFF"
                        )

                        // Stock queries, low-stock reports, audit history
                        .requestMatchers("/api/inventory/**")
                        .authenticated()

                        // Protect any other application endpoints
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(new BCryptPasswordEncoder(10));

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

