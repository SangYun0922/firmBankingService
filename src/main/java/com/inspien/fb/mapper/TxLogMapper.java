package com.inspien.fb.mapper;

import com.inspien.fb.domain.TxLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TxLogMapper {
    public boolean logAdd(TxLog log);
}
