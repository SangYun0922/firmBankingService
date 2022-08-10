package com.inspien.fb.mapper;

import com.inspien.fb.domain.TxStat;
import com.inspien.fb.domain.TxTrace;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TxTraceMapper {
    public int count(); //데이터 개수 가져오기
    public List<TxTrace> selectMany(int Start, int Limit); //readMany
}
