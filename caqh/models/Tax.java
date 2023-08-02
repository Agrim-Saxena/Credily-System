package com.credv3.caqh.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class Tax {
	
	@JsonProperty("GroupName")
	private String GroupName;

	@JsonProperty("TaxType")
	public TaxType TaxType;

	@JsonProperty("PrimaryFlag")
	public int PrimaryFlag;
	
	@JsonProperty("TaxID")
	public int TaxID;
}
