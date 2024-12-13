package com.stockwise.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderModel {
    
    private String id;

	private Object aud;

	private String email;

	private String name;

	private String picture;

	private String sub;

	private String iss;
}
