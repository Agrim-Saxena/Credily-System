package com.credv3.caqh.models;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import com.credv3.common.entities.Provider;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

//@Entity
//@Table(name = "caqh_provider_disclosure_answer")
public @Data class DisclosureAnswer {
	@Id
	@Column(name = "caqh_provider_disclosure_answer_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "disclosure_question_id")
	private DisclosureQuestion disclosureQuestion;
	@Lob
	@Column(name = "answer")
	private String answer;
	@Lob
	@Column(name = "disclosure_explanation")
	private String DisclosureExplanation;
	@JsonIgnore
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "provider_uuid")
	private Provider caqhProvider;
}
