package com.inspien.fb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenRequest {

//	"api_key" : "7242191d-865c-48df-aa02-e3cf10bffd6d", "org_code" : "10000262", "drw_bank_code" : "004", "telegram_no" : 3,
		
	private String api_key;
	private String org_code;
	private String drw_bank_code;
	private long telegram_no;
//	private String msg_id;
}