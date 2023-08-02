package com.credv3.caqh.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class CAQHHospital {
	
	@JsonProperty("HospitalName")
	public String HospitalName;
	
	@JsonProperty("NonAHAHospitalName")
	public String NonAHAHospitalName;
	
	@JsonProperty("Address")
	public String Address;
	
	@JsonProperty("Address2")
	public String Address2;
	
	@JsonProperty("City")
	public String City;
	
	@JsonProperty("State")
	public String State;
	
	@JsonProperty("ZipCode")
	public Object ZipCode;
	
	@JsonProperty("Country")
	public CaqhCountry country;
	
	@JsonProperty("StartDate")
	public Date StartDate;
	
	@JsonProperty("EndDate")
	public Date EndDate;
	
	
	
	@JsonProperty("HospitalRecordType")
	public String HospitalRecordType;
	
	@JsonProperty("AHAHospitalID")
	public int AHAHospitalID;
	
	@JsonProperty("TemporaryPrivilegesFlag")
	public int TemporaryPrivilegesFlag;
	
	@JsonProperty("FaxNumber")
	public String FaxNumber;
	
	
	
	@JsonProperty("UnrestrictedPrivilegesFlag")
	public int UnrestrictedPrivilegesFlag;
	
	@JsonProperty("AdmittingContactPhoneNumber")
	public long AdmittingContactPhoneNumber;
	
	
	@JsonProperty("AdmittingPrivileges")
	public int AdmittingPrivileges;
	
	@JsonProperty("PrivilegeDescription")
	public String PrivilegeDescription;
	
	@JsonProperty("StaffCategory")
	public String StaffCategory;
	
	@JsonProperty("HospitalAffiliationType")
	public HospitalAffiliationType HospitalAffiliationType;
	
	
	
	
	@JsonProperty("AdmittingPrivileges")
	public int AdmittingArrangements;
	
	@JsonProperty("WhoAdmitsForyou")
	public String WhoAdmitsForyou;
	
	@JsonProperty("FirstName")
	public String firstName;
	
	@JsonProperty("LastName")
	public String LastName;
	
	@JsonProperty("PhoneNumber")
	public String PhoneNumber;
	
	@JsonProperty("EmailAddress")
	public String EmailAddress;
	
	@JsonProperty("IsProviderSpecialtySameAsYourSpecialty")
	public int IsProviderSpecialtySameAsYourSpecialty;
	
	@JsonProperty("AdmittingArrangementDesc")
	public String AdmittingArrangementDesc;
	
	@JsonProperty("GroupName")
	public String GroupName;
	
	@JsonProperty("IndividualNpi")
	public String IndividualNpi;
	
	@JsonProperty("OrganisationNpi")
	public String OrganisationNpi;
	
	
	
	
	
	
	@JsonProperty("NonAdmittingAffiliations")
	public int NonAdmittingAffiliations;
	
	@JsonProperty("NonAdmittingAffiliationDesc")
	public String NonAdmittingAffiliationDesc;
	
	@JsonProperty("NonAdmittingPrivilegeStatus")
	public String NonAdmittingPrivilegeStatus;
	
	
}
