package com.inspien.fb.svc;

import com.inspien.fb.domain.BankMst;
import com.inspien.fb.domain.CustMst;
import com.inspien.fb.mapper.CustMstMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustMstService {

    @Autowired
    private CustMstMapper custMstMapper;

    public int insertData(CustMst custMst) {
        return custMstMapper.insert(custMst);
    }
    public List<CustMst> readDataOne(String CustId) {
        return custMstMapper.selectOne(CustId);
    }

    public List<CustMst> readDataMany() {
        return custMstMapper.selectMany();
    }

    public int updateData(CustMst custMst) {
        return custMstMapper.update(custMst);
    }

    public int deleteData(String CustId) {
        return custMstMapper.delete(CustId);
    }
}
