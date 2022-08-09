package com.inspien.fb.mapper;

import com.inspien.fb.domain.CustMst;
import com.inspien.fb.domain.TxLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TxLogMapper {
    public List<TxLog> selectMany(); //readMany
}
