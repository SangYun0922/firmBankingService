package com.inspien.fb.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class TransferCheckRequest {

//	{
//"api_key" : "TESTKEY", "org_code" : "ORG00000",
//"org_telegram_no" : 3, "tr_dt" : "20181212", "drw_bank_code" : "088"
// }

    private String api_key;
    private String org_code;
    private long org_telegram_no;
    private String tr_dt;
    private String drw_bank_code;
}