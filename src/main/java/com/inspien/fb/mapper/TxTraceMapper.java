package com.inspien.fb.mapper;

import com.inspien.fb.domain.TxTrace;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TxTraceMapper {
    public boolean insertOrUpdateTxTrace(TxTrace txTrace);
}
