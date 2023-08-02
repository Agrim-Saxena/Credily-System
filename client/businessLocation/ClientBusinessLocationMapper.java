package com.credv3.client.businessLocation;

import java.util.UUID;

import com.credv3.common.entities.ClientBusinessInfo;
import com.credv3.common.entities.ClientBusinessLocation;

public class ClientBusinessLocationMapper {

	public ClientBusinessLocation mapClientBusinessLocation(ClientBusinessLocationRequest request) {
		
		ClientBusinessLocation clientBusinessLocation = new ClientBusinessLocation();
		
		clientBusinessLocation.setPracticeType(request.getPracticeType());
		clientBusinessLocation.setLegalBusinessName(request.getLegalBusinessName());
		clientBusinessLocation.setDba(request.getDba());
		clientBusinessLocation.setAddress1(request.getAddress1());
		clientBusinessLocation.setAddress2(request.getAddress2());
		clientBusinessLocation.setStateName(request.getStateName());
		clientBusinessLocation.setStateCode(request.getStateCode());
		clientBusinessLocation.setCity(request.getCity());
		clientBusinessLocation.setCountry(request.getCountry());
		clientBusinessLocation.setPhone(request.getPhone());
		clientBusinessLocation.setEmail(request.getEmail());
		clientBusinessLocation.setFax(request.getFax());
		clientBusinessLocation.setEntityType(request.getEntityType());
		clientBusinessLocation.setEin(request.getEin());
		clientBusinessLocation.setNpi(request.getNpi());
		clientBusinessLocation.setZipcode(request.getZipcode());
		clientBusinessLocation.setFacilityType(request.getFacilityType());
		clientBusinessLocation.setPrimaryTaxonomy(request.getPriTaxonomy());
		clientBusinessLocation.setSecondaryTaxonomy(request.getSecTaxonomy());
		clientBusinessLocation.setAdditionalTaxonomy(request.getAddTaxonomy());
		
		ClientBusinessInfo clientBusinessInfo = new ClientBusinessInfo();
		clientBusinessInfo.setUuid(UUID.fromString(request.getClientBusinessInfoUuid()));
		clientBusinessLocation.setClientBusinessInfo(clientBusinessInfo);
		
		return clientBusinessLocation;
	}
}
