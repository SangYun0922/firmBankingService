package com.inspien.fb.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TxTrace {
    private String CustId; //CustId와 TxDate 두개가 PK
    private String TxDate;
    private String TxSequence; //TelegramNo -> transaction 발생할때마다 ++
    private String TxStarted; //개시전문 여부
    private String OrgCd;

    //admin 페이지로 추가적으로 CustNm, BankNm을 보내준다.
    private String CustNm;
    private String BankNm;
}