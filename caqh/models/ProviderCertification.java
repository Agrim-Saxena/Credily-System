package com.credv3.caqh.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

//@Entity
//@Table(name = "caqh_provider_certification")
public @Data class ProviderCertification{
	@Id
	@Column(name = "caqh_provider_certification_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;
	
	@Column(name = "certification_flag")
    public int CertificationFlag;
    
	@Column(name = "certification_description")
    public String CertificationDescription;
}

