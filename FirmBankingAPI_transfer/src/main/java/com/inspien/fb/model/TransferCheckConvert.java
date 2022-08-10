package com.inspien.fb.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class TransferCheckConvert {
    private String api_key;
    private String org_code;
    private String tr_dt;
    private String drw_bank_code;
    private String msg_id;
}
