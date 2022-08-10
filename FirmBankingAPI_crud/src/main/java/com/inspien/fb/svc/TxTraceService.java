package com.inspien.fb.svc;

import com.inspien.fb.domain.TxTrace;
import com.inspien.fb.mapper.TxTraceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TxTraceService {
    @Autowired
    private TxTraceMapper txTraceMapper;

    public List<TxTrace> readDataMany() {
        return txTraceMapper.selectMany();
    }
}
