package com.inspien.fb.mapper;

import com.inspien.fb.domain.TxStat;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TxStatMapper {
    public int count(); //데이터 개수 가져오기
    public List<TxStat> selectMany(int Start, int Limit); //readMany
}
