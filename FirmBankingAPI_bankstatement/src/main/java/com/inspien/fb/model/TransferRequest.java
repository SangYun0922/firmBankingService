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
public class TransferRequest {

//	"api_key" : "7242191d-865c-48df-aa02-e3cf10bffd6d", "org_code" : "10000262", "drw_bank_code" : "004", "telegram_no" : 3,
//	"drw_account" : "832210312031", "drw_account_cntn" : "문세인",
//	"rv_bank_code" : "081", "rv_account" : "46291012501007", "rv_account_cntn" : "핍랄샐",
//	"amount" : 1000,
//	"tr_dt" : "20220602", "tr_tm" : "155011"
		
	private String api_key;
	private String org_code;
	private String drw_bank_code;
	private long telegram_no;
	private String drw_account;
	private String drw_account_cntn;
	private String rv_bank_code;
	private String rv_account;
	private String rv_account_cntn;
	private long amount;
	private String tr_dt;
	private String tr_tm;
	private String msg_id;
	
}