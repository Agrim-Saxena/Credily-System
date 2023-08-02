package com.credv3.caqh.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

//@Entity
//@Table(name="caqh_provider_license")
public @Data class ProviderLicense{

	@Id
	@Column(name = "caqh_provider_license_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;
	@Column(name = "expiration_date")
	@JsonProperty("ExpirationDate")
	public Date ExpirationDate;
	@Column(name = "license_number")
	@JsonProperty("LicenseNumber")
	public String LicenseNumber;
	@Column(name = "state")
	@JsonProperty("State")
	public String State;
	@Column(name = "issue_date")
	@JsonProperty("IssueDate")
	public Date IssueDate;
	@Column(name = "license_type")
	@JsonProperty("LicenseType")
	public String LicenseType;
}
