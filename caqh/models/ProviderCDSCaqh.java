package com.credv3.caqh.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class ProviderCDSCaqh {

	@JsonProperty("CDSNumber")
	public String CDSNumber;
	
	@JsonProperty("State")
	public String State;
	
	@JsonProperty("ExpirationDate")
	public Date ExpirationDate;
	
	@JsonProperty("IssueDate")
	public Date IssueDate;
	
	@JsonProperty("CurrentlyPracticingFlag")
	public String CurrentlyPracticingFlag;
	
	
}
