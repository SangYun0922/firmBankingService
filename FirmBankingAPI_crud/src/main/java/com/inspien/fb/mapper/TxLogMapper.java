package com.inspien.fb.mapper;

import com.inspien.fb.domain.CustMst;
import com.inspien.fb.domain.TxLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TxLogMapper {
    public int count(); //데이터 개수 가져오기
    public List<TxLog> selectMany(int Start, int Limit); //readMany
    public List<TxLog> selectOne(String TxIdx); //readOne
}
