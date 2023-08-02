package com.credv3.userAccount;

import java.util.UUID;

import lombok.Data;

public @Data class ProfileDTO {

	private UUID userUUID;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private String businessPhone;
	private String title;
	private String extNumber;
	private long roleId;
	private String roleName;
}
