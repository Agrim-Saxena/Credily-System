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
//@Table(name = "caqh_provider_time_gap")
public @Data class TimeGap {

	@Id
	@Column(name = "caqh_provider_time_gap_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;

	@Column(name = "start_date")
	@JsonProperty("StartDate")
	public Date StartDate;

	@Column(name = "gap_explanation")
	@JsonProperty("GapExplanation")
	public String GapExplanation;

	@Column(name = "gap_description")
	@JsonProperty("GapDescription")
	public String GapDescription;

	@Column(name = "end_date")
	@JsonProperty("EndDate")
	public Date EndDate;
}
