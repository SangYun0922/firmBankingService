package com.inspien.fb.svc;

import com.inspien.fb.domain.TxLog;
import com.inspien.fb.mapper.TxLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TxLogService {
    @Autowired
    private TxLogMapper txLogMapper;

    public List<TxLog> readDataMany() {
        return txLogMapper.selectMany();
    }

}