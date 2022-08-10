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
}
