//20220707 updated
package com.inspien.fb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.inspien.fb.domain.CustMst;
import java.util.List;

@Mapper
public interface CustMstMapper {

    List<CustMst> getData();
}
