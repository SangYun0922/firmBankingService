//2022.07.07 created
package com.inspien.fb.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CustMst {
    private String CustId;
    private String CustNm;
    private String OrgCd;
    private String CallbackURL;
    private String InUse;

    public CustMst() {}
}
