package com.warehouse.inventory.WarehouseInventoryApplication.model;

import com.warehouse.inventory.WarehouseInventoryApplication.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Role role;

    @Override
    public String toString() {
        // never include the password
        return "Admin [id=" + id + ", username=" + username + ", role=" + role + "]";
    }
}
