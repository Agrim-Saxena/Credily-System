package com.credv3.client.contact;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientContact;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.ResponseEntityObject;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class ClientContactHandler extends CentralService{

	ClientContactMapper clientContactMapper = new ClientContactMapper();
	
	@Transactional(readOnly = false)
	public ResponseEntity<Object> createClientContact(ClientContactRequest request){
	
		try {
			ClientContact clientContact = clientContactMapper.mapContact(request);
			clientContactJpaRepo.save(clientContact);
			
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_CREATED), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("CLIENT CONTACT HANDLER :: ERROR IN METHOD 'createClientContact' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
	
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getAllClientContact(){
		try {
			List<ClientContact> contactList =  clientContactJpaRepo.findAll();
			ClientContactDTOMapper dtoMapper = Mappers.getMapper(ClientContactDTOMapper.class);
			List<ClientContactDTO> dtoMapperList = dtoMapper.map(contactList);
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, dtoMapperList), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("CLIENT HANDLER :: ERROR IN METHOD 'getAllClientContact' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
