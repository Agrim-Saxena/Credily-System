package com.credv3.client.businessLocation;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.common.entities.ClientBusinessLocation;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.ResponseEntityObject;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class ClientBusinessLocationHandler extends CentralService {
	
	ClientBusinessLocationMapper clientBusinessLocationMapper = new ClientBusinessLocationMapper();
	
	@Transactional(readOnly = false)
	public ResponseEntity<Object> createClientBusinessLocation(ClientBusinessLocationRequest request){
		try {
			ClientBusinessLocation clientBusinessLocation = clientBusinessLocationMapper.mapClientBusinessLocation(request);
			clientBusinessLocationJpaRepo.save(clientBusinessLocation);
			
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_CREATED), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("CLIENT BUSINESS LOCATION HANDLER :: ERROR IN METHOD 'createClientBusinessLocation' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional(readOnly=true)
	public ResponseEntity<Object> getAllClientBusinessLocation(){
		try {
			List<ClientBusinessLocation> businessLocationList = clientBusinessLocationJpaRepo.findAll();
			ClientBusinessLocationDTOMapper dtoMapper = Mappers.getMapper(ClientBusinessLocationDTOMapper.class);
			List<ClientBusinessLocationDTO> dtoMapperList = dtoMapper.map(businessLocationList);
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, dtoMapperList), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("CLIENT BUSINESS LOCATION HANDLER :: ERROR IN METHOD 'getAllClientBusinessLocation' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
		
		}
	}
	
}
