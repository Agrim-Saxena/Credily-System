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
//@Table(name = "caqh_provider_speciality")
public @Data class Specialty {
	@Id
	@Column(name = "caqh_provider_speciality_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;
	@JsonProperty("SpecialtyName")
	@Column(name = "specialty_name")
	public String SpecialtyName;
	@JsonProperty("SpecialtyType")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "caqh_provider_specialty_type_id")
	public SpecialtyType SpecialtyType;
	@Transient
	@JsonProperty("Specialty")
	public CAQHSpecialty Specialty;
	@JsonProperty("NUCCTaxonomyCode")
	@Column(name = "taxonomy_code")
	public String NUCCTaxonomyCode;
	
	@JsonProperty("BoardCertifiedFlag")
	@Column(name = "isBoardCertified")
	public int BoardCertifiedFlag=0;
	
	
	@JsonProperty("SpecialtyBoardName")
	@Column(name = "specialty_board_name")
	public String SpecialtyBoardName;
	
	
	@JsonProperty("CertificationDate")
	@Column(name = "certification_date")
	public Date CertificationDate;
	
	
	@JsonProperty("ExpirationDate")
	@Column(name = "expiration_date")
	public Date ExpirationDate;
	
	
	@JsonProperty("RecertificationDate")
	@Column(name = "recertification_date")
	public Date RecertificationDate;
	
}
