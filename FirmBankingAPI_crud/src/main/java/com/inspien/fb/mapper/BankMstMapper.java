//20220707 updated
package com.inspien.fb.mapper;

import com.inspien.fb.domain.BankMst;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BankMstMapper {

    public int insert(BankMst bankMst); //insertOne
    public List<BankMst> selectOne(String BankId); //readOne
    public List<BankMst> selectMany(); //readMany
    public int update(BankMst bankMst); //updateOne
    public int delete(String BankId); //delete
}




