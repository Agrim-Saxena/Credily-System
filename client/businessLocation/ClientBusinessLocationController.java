package com.credv3.client.businessLocation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/location", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientBusinessLocationController {

	@Autowired
	private ClientBusinessLocationHandler clientBusinessLocationHandler;
	
	@RequestMapping(value = "" , method = {RequestMethod.OPTIONS,RequestMethod.POST})
	public ResponseEntity<Object> createClientBusinessLocation(@RequestBody ClientBusinessLocationRequest request){
		return clientBusinessLocationHandler.createClientBusinessLocation(request);
	}
	@RequestMapping(value = "" , method = {RequestMethod.OPTIONS,RequestMethod.GET})
	public ResponseEntity<Object> getAllClientBusinessLocation(){
		return clientBusinessLocationHandler.getAllClientBusinessLocation();
	}
		

}
