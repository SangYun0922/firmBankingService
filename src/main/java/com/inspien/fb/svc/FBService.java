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

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@Component
public class FBService {

	private static DecimalFormat longFormatter = new DecimalFormat("000000000000"); // 12자리의 10진수의 decimalformat객체를
																					// 생성한다.
	private static String timezone = "Asia/Seoul";

	private static Map<String, Map<String, AtomicLong>> custCounter = new HashMap<String, Map<String, AtomicLong>>();

	@Autowired
	FileTelegramManager telegramMgr;

	public long getCounter(String orgCode) { // orgCode 기관 넘버를 받는다.
		// DateTimeFormatter.ofPattern("yyyy-MM-dd")는 20220624와 같이 현재 시간을 표현한다.
		String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(ZonedDateTime.now(ZoneId.of(timezone)));
		System.out.println("!@#$custCounter = " + custCounter);
		long txNo = 1;
		if (custCounter.containsKey(orgCode)) { // orgCode가 이미 custCounter에 key로 존재하고,
			if (custCounter.get(orgCode).containsKey(today)) { // orgCode 키에 대한 value값에 today가 포함된다면
				txNo = custCounter.get(orgCode).get(today).incrementAndGet(); // txNo는 today값에 포함된 txNo에 접근하여 increment한
																				// 후 Get을 한다.
			} else {
				// delete all data before put today
				custCounter.get(orgCode).clear(); // 만약 orgCode에 대한 value값이 today(오늘)이 아니라면, custCounter안에 있는 값을 삭제한다.
				custCounter.get(orgCode).put(today, new AtomicLong(txNo)); // 그 후, 새로 {orgCode={today={txNo}}로 값을 세팅한다.
			}
		} else { // 만약 orgCode키가 custCounter에 없다면, 즉 새로운 기관코드가 전달된다면,
			Map<String, AtomicLong> counter = new HashMap<String, AtomicLong>(); // counter라는 새로운 해쉬맵을 만들어서
			counter.put(today, new AtomicLong(txNo)); // 해쉬맵에 오늘의 날짜 정보와, txNo를 AtomicLong의 형태로 변환하여 세팅한다.
			custCounter.put(orgCode, counter); // 그후, custcounter를 업데이트 한다.
		}
		return txNo;
	}

	public TransferResponse transfer(TransferRequest req) throws Exception {

		boolean bNeedStart = true;
		// long txNo = getCounter(req.getOrg_code()); //org_code를 인자로 받아서 getCounter함수
		// 실행
		long txNo = telegramMgr.getNextCounter(req.getOrg_code());
		System.out.println("txNo1 = " + txNo);
		// 이 결과 txNo값이 getCounter의 리턴값으로 업데이트 된다.

		VANProxy proxy = new DuznProxyImpl(); // proxy라는 DuznProxyImpl클래스 객체를 생성한다.
		proxy.init(null, null);

		// call VAN API
		if (txNo == 1) { // txNo가 1이면, 먼저 https://localhost:9000/firmapi/rt/v1/ 해당 엔트리로 보내져 최초로 실행될때는
							// txNo에 대한 증감연산이 없으므로
							// txNo가 1이 반환된다.
			OpenRequest openReq = OpenRequest.builder() // 게시전문에 대하여 요청할 데이터를 세팅한다.
					.api_key(req.getApi_key()) // OpenReq의 멤버변수 api_key는 req(요청데이터)의 Api_key속성을 getter로 가져와 세팅
					.org_code(req.getOrg_code()) // OpenReq의 멤버변수 org_code는 req(요청데이터)의 Org_code속성을 getter로 가져와 세팅
					.drw_bank_code(req.getDrw_bank_code()) // OpenReq의 멤버변수 drw_bank_code는 req(요청데이터)의 Drw_bank_code속성을
															// getter로 가져와 세팅
					.telegram_no(txNo) // OpenReq의 멤버변수 telegram_no는 req(요청데이터)의 txNo로 세팅
					.build();
			proxy.open(openReq); // proxy객체의 멤버함수에 openReq를 파라미터로 실어서 open함수를 실행시킨다. 게시전문 쿼리 url에 openReq
			txNo = telegramMgr.getNextCounter(req.getOrg_code());
		}

		TransferResponse res = null;
		try {
			System.out.println("txNo2 = " + txNo);
			req.setTelegram_no(txNo); // txNo로 telegram_no를 업데이트
			res = proxy.transfer(req); // 그후 req로 인자로 proxy객체의 추상함수 transfer를 실행, 이 함수는 DuznProxyImpl에 구현되어있다.
			// 이번에는 게시전문이 아닌 transfer를 목표로한다.
			System.out.println("res = " + res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("fail to transfer => {}", e);
		}
		// res의 request_at필드에 현재 타임스탬프를 저장한다.
		res.setRequest_at(DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(ZonedDateTime.now(ZoneId.of(timezone))));
		return res; // FirmAPIController.java의 129라인으로 리턴
	}
}
