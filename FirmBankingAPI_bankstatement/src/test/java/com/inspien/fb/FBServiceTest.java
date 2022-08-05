package com.inspien.fb;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.inspien.fb.model.TransferRequest;
import com.inspien.fb.model.TransferResponse;
import com.inspien.fb.svc.FBService;
import com.inspien.fb.svc.FileTelegramManager;



@Slf4j
class FBServiceTest {
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGetCounter() {
		fail("Not yet implemented");
	}

	@Test
	void testTransfer() throws Exception {
		FBService svc = new FBService();
		
//		{
//			"api_key" : "7242191d-865c-48df-aa02-e3cf10bffd6d", "org_code" : "10000262", "drw_bank_code" : "004", "telegram_no" : 3,
//			"drw_account" : "832210312031", "drw_account_cntn" : "문세인",
//			"rv_bank_code" : "081", "rv_account" : "46291012501007", "rv_account_cntn" : "핍랄샐",
//			"amount" : 1000,
//			"tr_dt" : "20220602", "tr_tm" : "155011"
//		}
		
		LocalDateTime dateTime = LocalDateTime.now();
        String timeStamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(dateTime);

		TransferRequest transferReq = TransferRequest.builder()
				.api_key("7242191d-865c-48df-aa02-e3cf10bffd6d")
				.org_code("10000262")
				.drw_bank_code("004")
				.drw_account("832210312031")
				.drw_account_cntn("문세인")
				.rv_bank_code("081")
				.rv_account("46291012501007")
				.rv_account_cntn("핍랄샐")
				.amount(1000)
				.tr_dt(timeStamp.substring(0, 8))
				.tr_tm(timeStamp.substring(8))
				.build();
		TransferResponse response = svc.transfer(transferReq);
		
		assertNotNull(response);
		assertEquals(200, response.getStatus());
	}
	@Test
	void testTelegramManager() throws Exception {
		FileTelegramManager m = new FileTelegramManager();
		m.init();
		
		long no = m.getNextCounter("1234");

		for (int i=0; i<100; i++) {
			no = m.getNextCounter("1234");
		}
		log.debug("last telegram_no={}", no);
	}

	@Test //MariaDB와 테스트가 되는지 체크
	void testDB() {
		try
		{
			Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/firmbanking","inspien_test","123456");
			log.debug("con = {}", con);
		} catch (Exception e) {
		}
	}
}
