package com.inspien.fb.svc;

import com.inspien.fb.domain.TxLog;
import com.inspien.fb.mapper.TxLogMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class TxLogService {
    @Autowired
    private TxLogMapper txLogMapper;

    public boolean logAdd(TxLog log){
        return txLogMapper.logAdd(log);
    }
}
