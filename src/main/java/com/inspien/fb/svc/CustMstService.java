package com.inspien.fb.svc;

import com.inspien.fb.domain.CustMst;
import com.inspien.fb.mapper.CustMstMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustMstService {
    private CustMstMapper custMstMapper;

    public CustMstService(CustMstMapper custMstMapper) {
        this.custMstMapper = custMstMapper;
    }

    @Cacheable(value = "CustMst", key = "#OrgCd")
    //key를 #OrgCd로 설정하였는데 이럴경우 getData에서 받는 파라미터 id값대로 캐시데이터가 저장되어,
    // 추후 캐시데이터를 key값에 따라 부분 업데이트를 할 수 있다. 즉, CustMst라는 캐시테이블 안에 OrgCd를 키값으로 그에 해당되는 데이터가 value로 설정된다.
    public List<CustMst> getData(String OrgCd) {
        return custMstMapper.selectOne(OrgCd);
    }


    @CachePut(value = "CustMst", key = "#custMst.orgCd")
    public List<CustMst> updateData(CustMst custMst) {
        custMstMapper.update(custMst);
        return custMstMapper.selectOne(custMst.getOrgCd());
    }

}
