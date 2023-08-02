package com.credv3.caqh.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class PracticeAddress {

	
	@JsonProperty("Address")
	public String Address;
	
	@JsonProperty("Address2")
	public String Address2;
	
	@JsonProperty("City")
	public String City;
	
	@JsonProperty("County")
	public String County;
	
	@JsonProperty("State")
	public String State;
	
	@JsonProperty("PostalCode")
	public String PostalCode;
	
	@JsonProperty("Country")
	public Country Country;
	
	@JsonProperty("PhoneNumber")
	public String PhoneNumber;
	
	@JsonProperty("EmailAddress")
	public String EmailAddress;
	
	@JsonProperty("AddressType")
	public AddressType AddressType;
}

@Data class Country {
	
	@JsonProperty("CountryName")
	public String CountryName;
}
