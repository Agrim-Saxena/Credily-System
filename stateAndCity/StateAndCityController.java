package com.credv3.stateAndCity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/state", produces = MediaType.APPLICATION_JSON_VALUE)
public class StateAndCityController {
	
	@Autowired
	private StateAndCityHandler stateAndCityHandler;
	
	@RequestMapping(value = "", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public String getStateCode(
			@RequestParam(value = "stateCode") String stateCode){
		return stateAndCityHandler.getStateCode(stateCode);
	}

}
