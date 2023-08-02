package com.credv3.caqh.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class ProviderMedicareCaqh {

	@JsonProperty("MedicareNumber")
    public String MedicareNumber;
	
	@JsonProperty("State")
	public String State;
}
