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
public class TransferResponse {
	public TransferResponse() {}
	public TransferResponse(int status, String error_code, String error_message) {}
	
	private int status;
	private String natv_tr_no;
	private String request_at;
	private long amount;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String error_code;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	private String error_message;
	
	
}