package com.credv3.client.businessInfo;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.credv3.common.entities.UserAccount;

import lombok.Data;

public @Data class ClientBusinessInfoDTO {

	private UUID uuid;
	private String practiceType;
	private String legalBusinessName;
	private String dba;
	private String phone;
	private String email;
	private String fax;
	private String entityType;
	private String ein;
	private String clientBusinessLocation;
	private Date createdDate;
	private UserAccount owner;
	private Map<String, Object> clientOwner;
}
