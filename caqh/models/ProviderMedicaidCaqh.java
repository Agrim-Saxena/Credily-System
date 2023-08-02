package com.credv3.caqh.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class ProviderMedicaidCaqh {

	@JsonProperty("MedicaidNumber")
    public String MedicaidNumber;
	
	@JsonProperty("State")
	public String State;
}
