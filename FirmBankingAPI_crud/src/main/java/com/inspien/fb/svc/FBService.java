package com.inspien.fb.svc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspien.fb.model.OpenRequest;
import com.inspien.fb.model.TransferRequest;
import com.inspien.fb.model.TransferResponse;

//2022.07.01 StatementRequest, StatementResponse added
import com.inspien.fb.model.StatementRequest;
import com.inspien.fb.model.StatementResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@Component
public class FBService{

	private static DecimalFormat longFormatter = new DecimalFormat("000000000000");
	private static String timezone = "Asia/Seoul";

	private static Map<String , Map<String, AtomicLong> > custCounter = new HashMap<String , Map<String, AtomicLong>>();

	@Autowired
	FileTelegramManager telegramMgr;
	
	public long getCounter(String orgCode) {
		String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(ZonedDateTime.now(ZoneId.of(timezone)));
		
		long txNo = 1;
		if(custCounter.containsKey(orgCode)) {
			if(custCounter.get(orgCode).containsKey(today)) {
				txNo = custCounter.get(orgCode).get(today).incrementAndGet();
			}
			else {
				// delete all data before put today
				custCounter.get(orgCode).clear();
				custCounter.get(orgCode).put(today, new AtomicLong(txNo));
			}
		}
		else {
			Map<String, AtomicLong> counter = new HashMap<String, AtomicLong>();
			counter.put(today, new AtomicLong(txNo));
			custCounter.put(orgCode, counter);
		}
		return txNo;
	}
	public TransferResponse transfer(TransferRequest req) throws Exception {
		
		boolean bNeedStart = true;
//		long txNo = getCounter(req.getOrg_code());

		long txNo = telegramMgr.getNextCounter(req.getOrg_code());
		
		VANProxy proxy = new DuznProxyImpl();
		proxy.init(null	, null);

		// call VAN API
		if(txNo == 1) {
			OpenRequest openReq = OpenRequest.builder()
					.api_key(req.getApi_key())
					.org_code(req.getOrg_code())
					.drw_bank_code(req.getDrw_bank_code())
					.telegram_no(txNo)
					.build();
			proxy.open(openReq);
			txNo = telegramMgr.getNextCounter(req.getOrg_code());
		}
		
		TransferResponse res = null;
		try {
			req.setTelegram_no(txNo);
	 		res = proxy.transfer(req);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("fail to transfer => {}", e);
		} 
		
		res.setRequest_at(DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(ZonedDateTime.now(ZoneId.of(timezone))));
		return res;

	}

	//2022.07.01 transfer added
	public StatementResponse transfer(StatementRequest req, String callbackUrl) throws Exception {
		VANProxy proxy = new DuznProxyImpl();
		proxy.init(null	, null);
		StatementResponse res = null;
			log.info("req, callbackURL= {}, {}", req, callbackUrl);
			res = proxy.transfer(req, callbackUrl);

		return res;
	}
}
