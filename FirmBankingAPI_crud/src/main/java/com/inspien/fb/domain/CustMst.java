//2022.07.07 created
package com.inspien.fb.domain;

import lombok.*;

import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
@Data
@AllArgsConstructor
public class CustMst{
    private String CustId;
    private String CustNm;
    private String OrgCd;
    private String CallbackURL;
    private String ApiKey;
    private String PriContactNm;
    private String PriContactTel;
    private String PriContactEmail;
    private String SecContactNm;
    private String SecContactTel;
    private String SecContactEmail;
    private String TxSequence;
    private String InUse;
    private String CreatedAt;
    private String UpdatedAt;
    public CustMst() {}

}
