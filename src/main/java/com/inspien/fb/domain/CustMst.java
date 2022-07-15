//2022.07.07 created
package com.inspien.fb.domain;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
public class CustMst {
    private String CustId;
    private String CustNm;
    private String OrgCd;
    private String CallbackURL;
    private String InUse;
    private String isErr; //MariaDb에서 쿼리한 결과에 에러가 있는지 판별하기 위한 변수

    public CustMst() {}

}
