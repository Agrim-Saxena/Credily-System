package com.credv3.client.contact;

import java.util.List;
import org.mapstruct.Mapper;
import com.credv3.common.entities.ClientContact;

@Mapper
public interface ClientContactDTOMapper {

	List<ClientContactDTO> map(List<ClientContact> clientContact);
	ClientContactDTO map(ClientContact clientContact);
}
