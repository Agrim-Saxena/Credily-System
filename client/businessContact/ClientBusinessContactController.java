package com.credv3.client.businessContact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/business-contact", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientBusinessContactController {

	@Autowired
	private ClientBusinessContactHandler clientBusinessContactHandler;

	@RequestMapping(value = "" , method = {RequestMethod.OPTIONS,RequestMethod.POST})
	public ResponseEntity<Object> createClientBusinessContact(@RequestBody ClientBusinessContactRequest request){
		return clientBusinessContactHandler.createClientBusinessContact(request);
	}
	
	@RequestMapping(value = "" , method = {RequestMethod.OPTIONS,RequestMethod.GET})
    public ResponseEntity<Object> getAllClientBusinessContact(){
    	return clientBusinessContactHandler.getAllClientBusinessContact();
    }
}
