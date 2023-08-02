package com.credv3.caqh.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

//@Entity
//@Table(name = "caqh_provider_specialty_type")
public @Data class SpecialtyType{
	
	@Id
	@Column(name = "caqh_provider_specialty_type_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;
	
	@Column(name = "specialty_type_description")
    public String SpecialtyTypeDescription;
}

