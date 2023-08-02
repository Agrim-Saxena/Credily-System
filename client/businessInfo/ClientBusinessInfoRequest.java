package com.credv3.client.businessInfo;

import java.util.ArrayList;
import java.util.List;

import com.credv3.client.businessAddress.ClientBusinessAddressRequest;
import com.credv3.client.businessLocation.ClientBusinessLocationRequest;
import com.credv3.client.contact.ClientContactRequest;
import com.credv3.roleManagement.ModuleRequest;

import lombok.Data;

public @Data class ClientBusinessInfoRequest {
	
	private String practiceType;
	private String legalBusinessName;
	private String dba;
	private String phone;
	private String email;
	private String fax;
	private String entityType;
	private String ein;
	private String addressLine1;
	private String addressLine2;
	private String stateName;
	private String stateCode;
	private String city;
	private String country;
	private String zipcode;
	private ClientBusinessLocationRequest locationReq;
	private ClientBusinessAddressRequest addressReq;
	private List<ClientContactRequest> contactReqList; 
	private List<String> cliaUrlList;
	private List<String> cliaWaiverUrlList;
	private List<ModuleRequest> moduleRequestList = new ArrayList<>();
	private String userAccountUuid;
}
