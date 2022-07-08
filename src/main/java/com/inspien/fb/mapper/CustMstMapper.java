//20220707 updated
package com.inspien.fb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.inspien.fb.domain.CustMst;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface CustMstMapper {

    List<CustMst> getData(String OrgCd);

}
