package com.credv3.client.businessAddress;

import lombok.Data;

public @Data class ClientBusinessAddressRequest {

	private String addressLine1;
	private String addressLine2;
	private String stateName;
	private String stateCode;
	private String city;
	private String country;
	private String phone;
	private String email;
	private String fax;
	private String type;
	private String clientBusinessInfoUuid;
}
