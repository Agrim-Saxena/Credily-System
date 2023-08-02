package com.credv3.caqh.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class GenderLimitation {
	@JsonProperty("GenderLimitationDescription")
	private String GenderLimitationDescription;
}
