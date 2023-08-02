package com.credv3.caqh.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

//@Entity
//@Table(name = "caqh_provider_insurance")
public @Data class Insurance {
	@Id
	@Column(name = "caqh_provider_insurance_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;
	@Column(name = "Address")
	@JsonProperty("Address")
	public String Address;
	@Column(name = "policy_number")
	@JsonProperty("PolicyNumber")
	public String PolicyNumber;
	@Column(name = "insurance_carrier_name")
	@JsonProperty("InsuranceCarrierName")
	public String InsuranceCarrierName;
	@Column(name = "city")
	@JsonProperty("City")
	public String City;
	@Column(name = "end_date")
	@JsonProperty("EndDate")
	public Date EndDate;
	@Column(name = "affiliated_organization_name")
	@JsonProperty("AffiliatedOrganizationName")
	public String AffiliatedOrganizationName;
	@Column(name = "original_start_date")
	@JsonProperty("OriginalStartDate")
	public Date OriginalStartDate;
	@Column(name = "coverage_amount_aggregate")
	@JsonProperty("CoverageAmountAggregate")
	public int CoverageAmountAggregate;
	@Column(name = "start_date")
	@JsonProperty("StartDate")
	public Date StartDate;
	@Column(name = "state")
	@JsonProperty("State")
	public String State;
	@Column(name = "postal_code")
	@JsonProperty("PostalCode")
	public String PostalCode;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "insurance_type")
	@JsonProperty("InsuranceType")
	private InsuranceType InsuranceType;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "insurance_coverage_type")
	@JsonProperty("InsuranceCoverageType")
	private InsuranceCoverageType InsuranceCoverageType;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "insurance_country")
	@JsonProperty("Country")
	private CaqhCountry Country;

	@Column(name = "unlimited_coverage_flag")
	@JsonProperty("UnlimitedCoverageFlag")
	private int UnlimitedCoverageFlag;

	@Column(name = "coverage_amount_occurrence")
	@JsonProperty("CoverageAmountOccurrence")
	private int CoverageAmountOccurrence;
	@JsonProperty("IndividualCoverageFlag")
	private int IndividualCoverageFlag;
}
