package com.inspien.fb.svc;

import com.inspien.fb.domain.CustMst;
import com.inspien.fb.mapper.CustMstMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Getter
@Setter
public class CustMstService {
    @Autowired
    private CustMstMapper custMstMapper;

    public List<CustMst> getData(String OrgCd) {
        return  custMstMapper.select(OrgCd);
    }

}
