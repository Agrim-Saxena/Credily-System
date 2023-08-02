package com.credv3.client.businessAddress;

import java.util.List;

import org.mapstruct.Mapper;

import com.credv3.common.entities.ClientBusinessAddress;

@Mapper
public interface ClientBusinessAddressDTOMapper {

	List<ClientBusinessAddressDTO> map(List<ClientBusinessAddress> clientBusinessAddress);
	ClientBusinessAddressDTO map(ClientBusinessAddress clientBusinessAddress);
}
