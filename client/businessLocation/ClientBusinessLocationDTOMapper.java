package com.credv3.client.businessLocation;

import java.util.List;

import org.mapstruct.Mapper;

import com.credv3.common.entities.ClientBusinessLocation;

@Mapper
public interface ClientBusinessLocationDTOMapper {

	List<ClientBusinessLocationDTO> map(List<ClientBusinessLocation> clientBusinessLocation);
	ClientBusinessLocationDTO map(ClientBusinessLocation clientBusinessLocation);
}
