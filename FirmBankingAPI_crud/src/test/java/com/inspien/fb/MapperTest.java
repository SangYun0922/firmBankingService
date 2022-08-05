package com.inspien.fb;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inspien.fb.domain.CustMst;
import com.inspien.fb.mapper.CustMstMapper;
import com.inspien.fb.svc.CustMstService;
import com.inspien.fb.svc.GetClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
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
    CustMstService custMstService;
    @Autowired
    CustMstMapper custMstMapper;

    @Autowired
    GetClient getClient;

    @Test //AWS RDS MariaDB와 연결 되는지 테스트
    public void testDB() {
        try
        {
            Connection con = DriverManager.getConnection("jdbc:mariadb://firmbankingapi.cwtbzbyluijp.ap-northeast-2.rds.amazonaws.com/firmbanking","inspien_test","12345678");
            log.debug("con = {}", con);
        } catch (Exception e) {
        }
    }

    @Test //AWS RDS MariaDB와 update가 되는지 테스트
    public void testOfUpdate() {
        try
        {
            CustMst custMst = new CustMst();
            log.info("CustMst = {}", custMst);
            log.info("custMstMapper = {}",custMstMapper.update(custMst));
        } catch (Exception e) {
            log.error("{}", e);
        }
    }

    @Test
    public void testCallAPIGet() throws URISyntaxException, IOException, ParseException {
        String response = getClient.callAPIGet("http://127.0.0.1:6901/eureka/apps");
        JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        log.info("jsonResponse : {}", jsonResponse);
    }
}
