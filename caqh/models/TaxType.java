package com.credv3.caqh.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class TaxType {
	
	@JsonProperty("TaxTypeDescription")
	public String TaxTypeDescription;
}
