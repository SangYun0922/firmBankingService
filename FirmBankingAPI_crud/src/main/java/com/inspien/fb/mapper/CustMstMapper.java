//20220707 updated
package com.inspien.fb.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.inspien.fb.domain.CustMst;
import java.util.List;

@Mapper
public interface CustMstMapper {

    public int insert(CustMst custMst); //insertOne
    public List<CustMst> selectOne(String CustId); //readOne
    public List<CustMst> selectMany(); //readMany
    public int update(CustMst params); //updateOne
    public int delete(String CustId); //delete
}




