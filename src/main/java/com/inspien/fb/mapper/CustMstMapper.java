//20220707 updated
package com.inspien.fb.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.inspien.fb.domain.CustMst;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CustMstMapper {

    public List<CustMst> selectOne(String OrgCd); //read
    public int update(CustMst params); //updateOne
}




