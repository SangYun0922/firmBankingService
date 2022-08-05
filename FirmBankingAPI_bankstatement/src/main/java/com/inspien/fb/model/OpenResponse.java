package com.inspien.fb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class OpenResponse {
	public OpenResponse(){}
	private int status;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String drw_bank_code;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String open_state;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String error_code;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String error_message;
	
}