package com.inspien.fb.mapper;

import com.inspien.fb.domain.TxTrace;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TxTraceMapper {
    public boolean upsertTxTrace(TxTrace txTrace);
    public String selectTxTrace(@Param("custId") String custId, @Param("today") String today);
    public boolean isExistTxTrace(@Param("custId") String custId, @Param("today") String today);
    public void minusTxTrace(@Param("custId") String custId, @Param("today") String today);
}
