package com.credv3.userAccount;

import java.util.Date;
import java.util.UUID;

import lombok.Data;

public @Data class UserAccountRequest {

	private String userName;
	private String password;
	private String type;
	private UUID userId;
	private Date lastLoginDt;
	private long roleId ;
}
