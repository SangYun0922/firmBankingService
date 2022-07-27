package com.inspien.fb.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter

public class TxLog {
    //VO
    private String TxIdx; //timestamp + seq
    private String CustId; //?
    private String TxDate; //now
    private String TelegramNo; //telegram_no
    private String TxType; //?
    private String BankCd; //rv_bank_code
    private long Size; //header content-length
    private BigDecimal RoundTrip; //
    private int StmtCnt; //
    private Character Status;
    private Timestamp StartDT;
    private Timestamp EndDT;
//    private String EncData;
    private byte[] EncData;

    public TxLog(){}
}
