package com.inspien.fb.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TxStat {
    private String CustId;
    private String TxDate;
    private String BankCd;
    private String TxType;
    private long TxCnt; //unsigned mediumint
    private long TxSize; //unsigned int

}
