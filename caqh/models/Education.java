package com.credv3.caqh.models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

//@Entity
//@Table(name = "caqh_provider_education")
public @Data class Education {
	@Id
	@Column(name = "caqh_provider_education_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;

	@Column(name = "institution_name")
	@JsonProperty("InstitutionName")
	public String InstitutionName;

	@Column(name = "address")
	@JsonProperty("Address")
	public String Address;

	@Column(name = "education_type_name")
	@JsonProperty("EducationTypeName")
	public String EducationTypeName;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "degree_id")
	@JsonProperty("Degree")
	public Degree Degree;

	@Column(name = "postal_code")
	@JsonProperty("PostalCode")
	public String PostalCode;

	@Column(name = "city")
	@JsonProperty("City")
	public String City;

	@Column(name = "end_date")
	@JsonProperty("EndDate")
	public Date EndDate;

	@Column(name = "start_date")
	@JsonProperty("StartDate")
	public Date StartDate;

	@Column(name = "state")
	@JsonProperty("State")
	public String State;

	@Column(name = "program_completed_flag")
	@JsonProperty("ProgramCompletedFlag")
	public int ProgramCompletedFlag;

	@Column(name = "completion_date")
	@JsonProperty("CompletionDate")
	public Date CompletionDate;

	@Transient
	@JsonProperty("Country")
	private CaqhCountry Country;
}
