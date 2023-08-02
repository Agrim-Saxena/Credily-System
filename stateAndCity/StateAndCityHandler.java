package com.credv3.stateAndCity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.credv3.common.entities.UsState;

@Component
public class StateAndCityHandler {
	
	@Autowired
	public StateJPARepo stateJPARepo;
	
	public String getStateCode(String stateCode){
		try {
			UsState usState = stateJPARepo.findTop1ByStateCode(stateCode);
			return usState.getStateName();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
