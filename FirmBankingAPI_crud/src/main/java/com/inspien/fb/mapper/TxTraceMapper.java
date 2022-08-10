package com.inspien.fb.mapper;

import com.inspien.fb.domain.TxStat;
import com.inspien.fb.domain.TxTrace;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TxTraceMapper {
    public List<TxTrace> selectMany(); //readMany
}
