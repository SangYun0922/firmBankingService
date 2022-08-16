package com.inspien.fb.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@Data
public class TxLog {
    //VO
    private String TxIdx;
    private String CustId;
    private String OrgCd;
    private String TxDate;
    private String TelegramNo;
    private String MsgId;
    private String TxType; //transfer = 1; read = 2; bankstate = 3
    private String BankCd;
    private long Size;
    private BigDecimal RoundTrip;
    private Integer StmtCnt;
    private String Status;
    private Timestamp StartDT;
    private Timestamp EndDT;
    private String EncData;
//    private byte[] EncData;
    private String NatvTrNo;
    private String ErrCode;
    private String ErrMsg;

    public TxLog(){}
}
