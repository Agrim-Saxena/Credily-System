package com.credv3.caqh.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

//@Entity
//@Table(name = "caqh_provider_insurance_coverage_type")
public @Data class InsuranceCoverageType {
	@Id
	@Column(name = "caqh_provider_insurance_coverage_type_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;
	@Column(name = "insurance_coverage_type_description")
	@JsonProperty("InsuranceCoverageTypeDescription")
	private String InsuranceCoverageTypeDescription;
}
