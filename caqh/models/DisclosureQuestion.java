package com.credv3.caqh.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

//@Entity
//@Table(name = "caqh_provider_disclosure_question")
public @Data class DisclosureQuestion {
	@Id
	@Column(name = "caqh_provider_disclosure_question_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;
	@Column(name = "question")
	private String question;
	@Transient
	private DisclosureAnswer disclosureAnswer;
}
