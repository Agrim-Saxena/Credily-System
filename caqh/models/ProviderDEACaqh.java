package com.credv3.caqh.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class ProviderDEACaqh {
	
	@JsonProperty("DEANumber")
	public String DEANumber;
	
	@JsonProperty("State")
	public String State;
	
	@JsonProperty("ExpirationDate")
	public Date ExpirationDate;
	
	@JsonProperty("IssueDate")
	public Date IssueDate;
}
