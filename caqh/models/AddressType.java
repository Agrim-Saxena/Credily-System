package com.credv3.caqh.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class AddressType {

	@JsonProperty("AddressTypeDescription")
	public String AddressTypeDescription;
}
