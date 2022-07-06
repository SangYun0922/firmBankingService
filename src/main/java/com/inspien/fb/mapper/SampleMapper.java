//20220705 added
package com.inspien.fb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SampleMapper {

    @Select("SELECT * FROM CustMst WHERE OrgCd = '10000262'")
    String selectSampleData();

}
