package com.inspien.fb.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Data
public class TxStat {
    private String CustId;
    private String TxDate;
    private String BankCd;
    private String TxType;
    private long TxCnt; //unsigned mediumint
    private long TxSize; //unsigned int
    private String OrgCd;

    //admin 페이지로 추가적으로 CustNm, BankNm을 보내준다.
    private String CustNm;
    private String BankNm;
}
