package com.credv3.client.businessAddress;

import java.util.UUID;

import com.credv3.common.entities.ClientBusinessAddress;
import com.credv3.common.entities.ClientBusinessInfo;

public class ClientBusinessAddressMapper {

	public ClientBusinessAddress mapClientBusinessAddress(ClientBusinessAddressRequest request) {
		
		ClientBusinessAddress clientBusinessAddress = new ClientBusinessAddress();
		
		clientBusinessAddress.setAddressLine1(request.getAddressLine1());
		clientBusinessAddress.setAddressLine2(request.getAddressLine2());
		clientBusinessAddress.setStateName(request.getStateName());
		clientBusinessAddress.setStateCode(request.getStateCode());
		clientBusinessAddress.setCity(request.getCity());
		clientBusinessAddress.setCountry(request.getCountry());
		clientBusinessAddress.setPhone(request.getPhone());
		clientBusinessAddress.setEmail(request.getEmail());
		clientBusinessAddress.setFax(request.getFax());
		clientBusinessAddress.setType(request.getType());
		
		ClientBusinessInfo clientBusinessInfo = new ClientBusinessInfo();
		clientBusinessInfo.setUuid(UUID.fromString(request.getClientBusinessInfoUuid()));
		clientBusinessAddress.setClientBusinessInfo(clientBusinessInfo);
		
		return clientBusinessAddress;
	}
	
}
