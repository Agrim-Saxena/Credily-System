package com.credv3.caqh.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

public @Data class Gender{
	
    @JsonProperty("GenderDescription")
    public String GenderDescription;
}

