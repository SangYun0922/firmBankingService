package com.inspien.fb.mapper;

import com.inspien.fb.domain.TxStat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TxStatMapper {
    public boolean insertTxStat(TxStat txStat);
}
