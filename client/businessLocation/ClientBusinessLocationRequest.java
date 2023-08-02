package com.credv3.client.businessLocation;

import lombok.Data;

public @Data class ClientBusinessLocationRequest {

	private String practiceType;
	private String legalBusinessName;
	private String dba;
	private String address1;
	private String address2;
	private String stateName;
	private String stateCode;
	private String city;
	private String country;
	private String phone;
	private String email;
	private String fax;
	private String entityType;
	private String ein;
	private String npi;
	private String clientBusinessInfoUuid;
	
	private String zipcode;
	private String facilityType;
	private String priTaxonomy;
	private String secTaxonomy;
	private String addTaxonomy;
	
	
}
