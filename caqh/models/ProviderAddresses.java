package com.credv3.caqh.models;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

//@Entity
//@Table(name = "caqh_provider_address")
public @Data class ProviderAddresses{
	@Id
	@Column(name = "caqh_provider_address_id")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(strategy = "native", name = "native")
	private long id;
	@JsonProperty("Address")
	@Column(name = "address")
    public String Address;
	@JsonProperty("State")
	@Column(name = "state")
    public String State;
	@JsonProperty("PostalCode")
	@Column(name = "postal_code")
    public String PostalCode;
	@JsonProperty("PhoneNumber")
	@Column(name = "phone_number")
    public String PhoneNumber;
	@JsonProperty("City")
	@Column(name = "city")
    public String City;
	@JsonProperty("County")
	@Column(name = "county")
    public String County;
	@JsonProperty("EmailAddress")
	@Column(name = "email_address")
    public String EmailAddress;
}

