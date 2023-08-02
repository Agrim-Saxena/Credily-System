package com.credv3.caqh.models;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class BirthCountry{
	
	@JsonProperty("CountryName")
    public String CountryName;
}

