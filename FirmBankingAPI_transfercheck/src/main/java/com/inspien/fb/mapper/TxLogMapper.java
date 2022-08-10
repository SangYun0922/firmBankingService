package com.inspien.fb.mapper;

import com.inspien.fb.domain.TxLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TxLogMapper {
    public boolean logAdd(@Param("key") String key, @Param("logData") TxLog logData);
    public String selectMsgId(@Param("msgId") String msgId, @Param("txDate")String txDate);
}
