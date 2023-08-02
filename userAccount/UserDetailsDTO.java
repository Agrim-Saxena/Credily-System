package com.credv3.userAccount;

import lombok.Data;

public @Data class UserDetailsDTO {
	private String name;
	private String email;
	private String imageUrl;
	private String redis;
}
