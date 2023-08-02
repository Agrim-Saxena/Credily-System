package com.credv3.caqh.models;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class Practice {
	
	@JsonProperty("PracticeLocationId")
	private String PracticeLocationId;
	
	@JsonProperty("PracticeName")
	public String PracticeName;
	
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
	
	@JsonProperty("ZIP")
	public String Zip;
	
	@JsonProperty("ExtZip")
	public String ExtZip;
	
	@JsonProperty("PhoneNumber")
	public String PhoneNumber;
	
	@JsonProperty("EmailAddress")
	public String EmailAddress;
	
	@JsonProperty("AffliationDescription")
	public String AffliationDescription;
	
	@JsonProperty("StartDate")
	public Date StartDate;
	
	@JsonProperty("PatientAppointmentPhoneNumber")
	public String PatientAppointmentPhoneNumber;
	
	@JsonProperty("W9PracticeName")
	public String W9PracticeName;
	
	@JsonProperty("PracticeTypeDescription")
	public String PracticeTypeDescription;
	
	@JsonProperty("FaxNumber")
	public String FaxNumber;
	
	@JsonProperty("PracticeWebsite")
	public String PracticeWebsite;
	
	@JsonProperty("AddressType")
	public AddressType AddressType;
	
	@JsonProperty("ElectronicBillingFlag")
	public int ElectronicBillingFlag;
	
	
	@JsonProperty("Tax")
	private Tax Tax;
	
//	@JsonProperty("Limitation")
//	public CAQHProviderLimitation Limitation;
	
//	@JsonProperty("Limitation")
//	public List<CAQHProviderLimitation> Limitation;
	
//	@JsonProperty("PracticeAddress")
//	public PracticeAddress PracticeAddress;
	
//	@JsonProperty("PracticeAddress")
//	public List<PracticeAddress> PracticeAddress;
	

}
