package com.inspien.fb.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Data
@AllArgsConstructor
public class BankMst{
    private String BankId;
    private String BankCd;
    private String BankNm;
    private String SwiftCd;
    private Timestamp CreatedAt;
    private Timestamp UpdatedAt;
    public BankMst() {}

}
