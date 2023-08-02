package com.credv3.client.businessInfo;

import java.util.List;
import org.mapstruct.Mapper;
import com.credv3.common.entities.ClientBusinessInfo;

@Mapper
public interface ClientBusinessInfoDTOMapper {

	List<ClientBusinessInfoDTO> map(List<ClientBusinessInfo> businessInfo);
	
	ClientBusinessInfoDTO map(ClientBusinessInfo clientBusinessInfo);
}
