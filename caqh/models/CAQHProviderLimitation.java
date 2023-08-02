package com.credv3.caqh.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class CAQHProviderLimitation {
	
	@JsonProperty("AgeLimitationFlag")
	private int AgeLimitationFlag;
	
	@JsonProperty("AgeLimitationMinimum")
	private int AgeLimitationMinimum;
	
	@JsonProperty("AgeLimitationMaximum")
	private int AgeLimitationMaximum;
	
	@JsonProperty("GenderLimitation")
	private GenderLimitation GenderLimitation;
	
	
}
