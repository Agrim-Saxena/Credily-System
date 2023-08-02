package com.credv3.client.businessInfo;

import java.util.Date;

import com.credv3.common.entities.ClientBusinessInfo;

public class ClientBusinessInfoMapper {
	
	public ClientBusinessInfo mapBusinessInfo(ClientBusinessInfoRequest request) {
		
		ClientBusinessInfo clientBusinessInfo = new ClientBusinessInfo();
		clientBusinessInfo.setAddressLine1(request.getAddressLine1());
		clientBusinessInfo.setAddressLine2(request.getAddressLine2());
		clientBusinessInfo.setCity(request.getCity());
		clientBusinessInfo.setStateCode(request.getStateCode());
		clientBusinessInfo.setCountry(request.getCountry());
		clientBusinessInfo.setZipcode(request.getZipcode());
		clientBusinessInfo.setPracticeType(request.getPracticeType());
		clientBusinessInfo.setLegalBusinessName(request.getLegalBusinessName());
		clientBusinessInfo.setDba(request.getDba());
		clientBusinessInfo.setPhone(request.getPhone());
		clientBusinessInfo.setEmail(request.getEmail());
		clientBusinessInfo.setFax(request.getFax());
		clientBusinessInfo.setEntityType(request.getEntityType());
		clientBusinessInfo.setEin(request.getEin());
		clientBusinessInfo.setCreatedDate(new Date());
		
		return clientBusinessInfo;
	}
}
