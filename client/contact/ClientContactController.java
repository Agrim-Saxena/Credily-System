package com.credv3.client.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/contact", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientContactController {

	@Autowired
	private ClientContactHandler clientContactHandler;
	
	@RequestMapping(value="", method = {RequestMethod.OPTIONS,RequestMethod.POST})
	public ResponseEntity<Object> createClientContact(@RequestBody ClientContactRequest request){
		return clientContactHandler.createClientContact(request);
	}
	
	@RequestMapping(value="", method = {RequestMethod.OPTIONS,RequestMethod.GET})
	public ResponseEntity<Object> getAllClientContact(){
		return clientContactHandler.getAllClientContact();
	}
}
