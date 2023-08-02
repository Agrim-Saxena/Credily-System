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
//@Table(name = "caqh_provider_work_history")
public @Data class WorkHistory {
	@Id
	@Column(name = "caqh_provider_work_history_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;

	@Column(name = "current_employer_flag")
	@JsonProperty("CurrentEmployerFlag")
	public int CurrentEmployerFlag;

	@Column(name = "address")
	@JsonProperty("Address")
	public String Address;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "caqh_provider_work_history_type_id")
	@JsonProperty("WorkHistoryType")
	public WorkHistoryType WorkHistoryType;

	@Column(name = "state")
	@JsonProperty("State")
	public String State;

	@Column(name = "postal_code")
	@JsonProperty("PostalCode")
	public String PostalCode;

	@Column(name = "city")
	@JsonProperty("City")
	public String City;

	

	@Column(name = "phone_number")
	@JsonProperty("PhoneNumber")
	public String PhoneNumber;
	
	@Column(name = "fax_number")
	@JsonProperty("FaxNumber")
	public String FaxNumber;
	
	@Column(name = "employer_name")
	@JsonProperty("EmployerName")
	public String EmployerName;

	@Column(name = "start_date")
	@JsonProperty("StartDate")
	public Date StartDate;

	@Column(name = "end_date")
	@JsonProperty("EndDate")
	public Date EndDate;

	@Column(name = "exit_explanation")
	@JsonProperty("ExitExplanation")
	public String ExitExplanation;

	@Column(name = "status_description")
	@JsonProperty("StatusDescription")
	public String StatusDescription;

	@Column(name = "gap_description")
	@JsonProperty("GapDescription")
	public String GapDescription;

	@Column(name = "gap_explanation")
	@JsonProperty("GapExplanation")
	public String GapExplanation;

	@Column(name = "gap_start_date")
	@JsonProperty("GapStartDate")
	public Date GapStartDate;

	@Column(name = "gap_end_date")
	@JsonProperty("GapEndDate")
	public Date GapEndDate;

	@Column(name = "is_gap")
	@JsonProperty("isGap")
	public int isGap;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "insurance_country")
	@JsonProperty("Country")
	private CaqhCountry Country;
}
