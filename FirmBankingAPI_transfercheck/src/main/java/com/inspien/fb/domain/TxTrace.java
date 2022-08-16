package com.inspien.fb.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TxTrace {
    private String CustId; //CustId와 TxDate 두개가 PK
    private String OrgCd;
    private String TxDate;
    private String TxSequence; //TelegramNo -> transaction 발생할때마다 ++
    private String TxStarted; //개시전문 여부
}
