package com.credv3.caqh.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class CaqhCountry {
	@JsonProperty("CountryName")
	private String CountryName;
}
