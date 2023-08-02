package com.credv3.caqh.models;

import java.util.Date;
import java.util.List;

import com.credv3.common.entities.Provider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class CAQHProvider {
	
	@JsonProperty("Id")
	private long Id;
	
	@JsonProperty("FirstName")
	public String FirstName;
	@JsonProperty("LastName")
	public String LastName;
	@JsonProperty("MiddleName")
	public String MiddleName;
	
	@JsonProperty("EmailAddress")
	public String EmailAddress;
	
	@JsonProperty("PrimaryPracticeState")
	public String PrimaryPracticeState;
	
	@JsonProperty("BirthDate")
	public Date BirthDate;
	
	@JsonProperty("BirthCity")
	public String BirthCity;
	
	@JsonProperty("BirthState")
	public String BirthState;
	
	@JsonProperty("BirthCountry")
	public BirthCountry BirthCountry;
	
	@JsonProperty("SSN")
	public int SSN;
	
	@JsonProperty("UPINFlag")
	public int UPINFlag;
	
	@JsonProperty("UPIN")
	public String UPIN;
	
	@JsonProperty("NPI")
	public int NPI;
	
	@JsonProperty("CitizenshipStatus")
	public String CitizenshipStatus;
	
	@JsonProperty("ProviderType")
	public ProviderType ProviderType;
	
	@JsonProperty("Gender")
	public Gender Gender;
	
	@JsonProperty("ProviderLicense")
	public List<ProviderLicense> ProviderLicense;
	
	@JsonIgnore
	@JsonProperty("provider")
	public Provider provider;
	
	@JsonProperty("WorkHistory")
	public List<WorkHistory> WorkHistory;
	
	@JsonProperty("Specialty")
	public List<Specialty> Specialty;
	
	@JsonProperty("HospitalAdmittingArrangements")
	public String HospitalAdmittingArrangements;
	
	@JsonProperty("SecondarySpecialtyFlag")
	public int SecondarySpecialtyFlag;
	
	@JsonProperty("Education")
	public List<Education> Education;

	@JsonProperty("HospitalPrivilegeFlag")
	public int HospitalPrivilegeFlag;
	
	@JsonProperty("NPIFlag")
	public int NPIFlag;
	
	@JsonProperty("Degree")
	public List<Degree> Degree;
	
	@JsonProperty("AffiliatedFlag")
	public int AffiliatedFlag;
	
	@JsonProperty("MedicareProviderFlag")
	public int MedicareProviderFlag;
	
	@JsonProperty("EthnicityDescription")
	public String EthnicityDescription;
	
	@JsonProperty("ProviderCertification")
	public List<ProviderCertification> ProviderCertification;
	
	@JsonProperty("TimeGap")
	public List<TimeGap> TimeGap;
	
	@JsonProperty("MedicaidProviderFlag")
	public int MedicaidProviderFlag;
	
	@JsonProperty("HospitalBasedFlag")
	public int HospitalBasedFlag;
	
	@JsonProperty("CDSFlag")
	public int CDSFlag;
	
	@JsonProperty("WorkHistoryGapFlag")
	public int WorkHistoryGapFlag;
	
	@JsonProperty("ProviderAttestID")
	public int ProviderAttestID;
	
	@JsonProperty("DelegatedFlag")
	public int DelegatedFlag;
	
	
	@JsonProperty("AttestDate")
	public Date AttestDate;
	
	@JsonProperty("OtherNameFlag")
	public int OtherNameFlag;
	
	@JsonProperty("Insurance")
	public List<Insurance> Insurance;
	
	@JsonProperty("DEAFlag")
	public int DEAFlag;
	
	@JsonProperty("FellowshipTrainingFlag")
	public int FellowshipTrainingFlag;
	
	@JsonProperty("ActiveMilitaryFlag")
	public int ActiveMilitaryFlag;
	
	@JsonProperty("disclosureQuestions")
	private List<DisclosureQuestion> disclosureQuestions;
	
	@JsonProperty("ProviderAddress")
	public List<ProviderAddresses> ProviderAddress;
	
	
	@JsonProperty("ProviderCDS")
	public List<ProviderCDSCaqh> ProviderCDS;
	
	@JsonProperty("ProviderDEA")
	public List<ProviderDEACaqh> ProviderDEA;
	
	@JsonProperty("ProviderMedicare")
	public List<ProviderMedicareCaqh> ProviderMedicare;
	
	@JsonProperty("ProviderMedicaid")
	public List<ProviderMedicaidCaqh> ProviderMedicaid;
	
	@JsonProperty("Practice")
	public List<Practice> Practice;
	
	@JsonProperty("Hospital")
	public List<CAQHHospital> Hospital;
}
