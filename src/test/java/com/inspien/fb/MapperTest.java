package com.inspien.fb;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import com.inspien.fb.domain.CustMst;
import com.inspien.fb.mapper.CustMstMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.inspien.fb.model.TransferRequest;
import com.inspien.fb.model.TransferResponse;
import com.inspien.fb.svc.FBService;
import com.inspien.fb.svc.FileTelegramManager;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
public class MapperTest {
    @Autowired
    private CustMstMapper custMstMapper;

    @Test //MariaDB와 테스트가 되는지 체크
    public void testDB() {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/firmbanking","inspien_test","123456");
            log.debug("con = {}", con);
        } catch (Exception e) {
        }
    }

    @Test
    public void testOfUpdate() {
         CustMst custMst = CustMst.builder()
                 .CustId("0000000004")
                 .CustNm("최지희")
                 .OrgCd("19990916")
                 .CallbackURL("https://localhost:9000/변경완료")
                 .InUse("N")
                 .build();
         log.info("CustMst = {}", custMst.getCustNm());
         int result = custMstMapper.update(custMst);
         if (result == 1) {
             System.out.println("update완료!");
         } else {
             System.out.println("update실패ㅠㅠ");
         }
    }
}
