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
    private String TxIdx; //timestamp_txtyp seq
    private String CustId;
    private String TxDate; //now date
    private String TelegramNo; //?
    private String MsgId;
    private String TxType; //transfer = 1; read = 2; bankstate = 3
    private String BankCd;
    private long Size; //header's content-length
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
