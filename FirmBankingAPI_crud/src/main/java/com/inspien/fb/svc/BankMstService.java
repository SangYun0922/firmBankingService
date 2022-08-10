package com.inspien.fb.svc;

import com.inspien.fb.domain.BankMst;
import com.inspien.fb.domain.CustMst;
import com.inspien.fb.mapper.BankMstMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BankMstService {

    @Autowired
    private BankMstMapper bankMstMapper;

    public int totalCount() { return bankMstMapper.count(); }
    public int insertData(BankMst bankMst) {
        return bankMstMapper.insert(bankMst);
    }
    public List<BankMst> readDataOne(String BankId) {
        return bankMstMapper.selectOne(BankId);
    }

    public List<BankMst> readDataMany(int Start, int Limit) {
        return bankMstMapper.selectMany(Start, Limit);
    }

    public int updateData(BankMst bankMst) {
        return bankMstMapper.update(bankMst);
    }

    public int deleteData(String BankId) {
        return bankMstMapper.delete(BankId);
    }
}
