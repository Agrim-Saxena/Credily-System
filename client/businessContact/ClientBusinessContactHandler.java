package com.credv3.client.businessContact;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.credv3.client.businessInfo.ClientBusinessInfoDTO;
import com.credv3.common.entities.ClientBusinessContact;
import com.credv3.common.entities.ClientBusinessInfo;
import com.credv3.common.entities.ClientContact;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.ResponseEntityObject;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class ClientBusinessContactHandler extends CentralService {
    @Transactional(readOnly = false)
	public ResponseEntity<Object> createClientBusinessContact(ClientBusinessContactRequest request){
		try {
			ClientBusinessContact clientBusinessContact = new ClientBusinessContact();	
			
			ClientBusinessInfo clientBusinessInfo = new ClientBusinessInfo();
			clientBusinessInfo.setUuid(request.getClientBusinessInfoUuid());
			clientBusinessContact.setClientBusinessInfo(clientBusinessInfo);
			
			ClientContact clientContact = new ClientContact();
			clientContact.setUuid(request.getClientContactUuid());
			clientBusinessContact.setClientContact(clientContact);
						
			clientBusinessContactJpaRepo.save(clientBusinessContact);
			return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS_CREATED), HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			log.info("CLIENT BUSINESS CONTACT HANDLER :: ERROR IN METHOD 'createClientBusinessContact' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
			
		}
	}
    
    @Transactional(readOnly=true)
    public ResponseEntity<Object> getAllClientBusinessContact(){
    	try {
    		List<ClientBusinessContact> reps = clientBusinessContactJpaRepo.findAll();
    		return new ResponseEntity<>(new ResponseEntityObject<>(true, ConstantExtension.SUCCESS, reps), HttpStatus.OK);
    	}catch(Exception e) {
    		e.printStackTrace();
			log.info("CLIENT BUSINESS CONTACT HANDLER :: ERROR IN METHOD 'getAllClientBusinessContact' : " + e.getMessage());
			return new ResponseEntity<>(new ResponseEntityObject<>(false, ConstantExtension.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    
}
