package com.credv3.client.businessAddress;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientBusinessAddress;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.ResponseEntityObject;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class ClientBusinessAddressHandler extends CentralService{

	ClientBusinessAddressMapper clientBusinessAddressMapper = new ClientBusinessAddressMapper();
	
	@Transactional(readOnly = false)
	public ResponseEntity<Object> createClientBusinessAddress(ClientBusinessAddressRequest request){
		try {
			ClientBusinessAddress clientBusinessAddress = clientBusinessAddressMapper.mapClientBusinessAddress(request);
			clientBusinessAddressJpaRepo.save(clientBusinessAddress);
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_CREATED), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("CLIENT BUSINESS ADDRESS HANDLER :: ERROR IN METHOD 'createClientBusinessAddress' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional(readOnly = true)
	public ResponseEntity<Object> getAllClientBusinessAddress(){
		try {
			List<ClientBusinessAddress> clientBusinessAddress = clientBusinessAddressJpaRepo.findAll();
			ClientBusinessAddressDTOMapper dtoMapper = Mappers.getMapper(ClientBusinessAddressDTOMapper.class);
			List<ClientBusinessAddressDTO> dtoMapperList = dtoMapper.map(clientBusinessAddress);
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, dtoMapperList), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("CLIENT BUSINESS ADDRESS HANDLER :: ERROR IN METHOD 'getAllClientBusinessAddress' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
