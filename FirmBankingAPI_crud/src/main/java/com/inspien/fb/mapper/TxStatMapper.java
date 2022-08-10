package com.inspien.fb.mapper;

import com.inspien.fb.domain.TxStat;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TxStatMapper {
    public List<TxStat> selectMany(); //readMany
}
