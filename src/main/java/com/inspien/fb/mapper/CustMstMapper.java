//20220707 updated
package com.inspien.fb.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.inspien.fb.domain.CustMst;
import org.springframework.cache.annotation.Cacheable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface CustMstMapper {

//    @Cacheable(cacheNames = "CustMst", key = "#OrgCd") //key를 #OrgCd로 설정하였는데 이럴경우 getData에서 받는 파라미터 id값대로 캐시데이터가 저장되어,
        // 추후 캐시데이터를 key값에 따라 부분 업데이트를 할 수 있다. 즉, CustMst라는 캐시테이블 안에 OrgCd를 키값으로 그에 해당되는 데이터가 value로 설정된다.
    List<CustMst> select(String OrgCd);
}




