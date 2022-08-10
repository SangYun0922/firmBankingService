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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.inspien.fb.WriteLogs;
import com.inspien.fb.domain.CustMst;
import com.inspien.fb.mapper.CustMstMapper;
import com.inspien.fb.mapper.TxTraceMapper;
import com.inspien.fb.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
	@Autowired
	WriteLogs writeLogs;
	@Autowired
	CustMstMapper custMstMapper;
	@Autowired
	TxTraceMapper txTraceMapper;

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
	public TransferResponse transfer(TransferRequest req) throws Exception { //transfer
		boolean bNeedStart = true;
		boolean openReqFlag = true; // 개시전문 여부(= 해당 컬럼 존재 여부 확인)
		String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(ZonedDateTime.now(ZoneId.of(timezone)));
//		long txNo = getCounter(req.getOrg_code());
		VANProxy proxy = new DuznProxyImpl();
		proxy.init(null	, null);

		List<CustMst> custMsts = custMstMapper.selectOne((req.getOrg_code()));
		String custId = custMsts.get(0).getCustId();
		openReqFlag = txTraceMapper.isExistTxTrace(custId,today);
		log.info("개시전문 여부 : {}",openReqFlag );

		if(!openReqFlag) { //개시전문
			log.info("start openReq");
			writeLogs.insertTxTraceLog(today,custId,1); //컬럼 생성 + 1로
			long txNo = 1;
			OpenRequest openReq = OpenRequest.builder()
					.api_key(req.getApi_key())
					.org_code(req.getOrg_code())
					.drw_bank_code(req.getDrw_bank_code())
					.telegram_no(txNo)
//					.msg_id(req.getMsg_id()) 개시전문시 msg_id 필요 x
					.build();
			proxy.open(openReq);

		}
		long txNo = telegramMgr.getNextCounter(req.getOrg_code());
		TransferResponse res = null;
		log.info("txNo : {}", txNo);
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

	public TransferCheckResponse transfer(TransferCheckRequest req) throws Exception {
		VANProxy proxy = new DuznProxyImpl();
		proxy.init(null	, null);
		TransferCheckResponse res = null;
		res = proxy.transfer(req);
		return res;
	}
}
