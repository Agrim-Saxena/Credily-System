package com.credv3.stateAndCity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.credv3.common.entities.UsState;

public interface StateJPARepo extends JpaRepository<UsState, Long> {

	UsState findTop1ByStateCode(String stateCode);
	
	@Query(value="SELECT us.state_name as stateName FROM us_state us WHERE us.state_code = ?1", nativeQuery = true)
	String getStateNameFromStateCode(String code);
	
}
