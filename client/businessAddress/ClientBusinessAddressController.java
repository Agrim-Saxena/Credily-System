package com.credv3.client.businessAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/address" , produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientBusinessAddressController {

	@Autowired
	private ClientBusinessAddressHandler clientBusinessAddressHandler;
	
	@RequestMapping(value = "" , method = {RequestMethod.OPTIONS,RequestMethod.POST})
	public ResponseEntity<Object> createClientBusinessAddress(@RequestBody ClientBusinessAddressRequest request){
		return clientBusinessAddressHandler.createClientBusinessAddress(request);
	}
	
	@RequestMapping(value = "" , method = {RequestMethod.OPTIONS,RequestMethod.GET})
	public ResponseEntity<Object> getAllClientBusinessAddress(){
		return clientBusinessAddressHandler.getAllClientBusinessAddress();
	}
}
