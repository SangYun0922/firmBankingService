package com.inspien.fb.svc;

import com.inspien.fb.domain.TxStat;
import com.inspien.fb.mapper.TxStatMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TxStatService {
    @Autowired
    private TxStatMapper txStatMapper;

    public int totalCount() { return txStatMapper.count(); }
    public List<TxStat> readDataMany(int Start, int Limit) {
        return txStatMapper.selectMany(Start, Limit);
    }

}
