package com.credv3.client.contact;

import lombok.Data;

public @Data class ClientContactDTO {

	private String firstName;
	private String lastName;
	private String phone;
	private String alternatePhone;
	private String email;
	private int isPrimary;
}
