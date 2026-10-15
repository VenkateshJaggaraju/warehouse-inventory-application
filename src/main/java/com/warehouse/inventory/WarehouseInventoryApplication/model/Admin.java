package com.warehouse.inventory.WarehouseInventoryApplication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

	@Id
	Integer id;
	String username;
	String password;

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + "]";
	}

}



