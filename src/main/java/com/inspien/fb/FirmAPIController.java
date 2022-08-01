package com.inspien.fb;

import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inspien.fb.domain.CustMst;
import com.inspien.fb.domain.TxLog;
import com.inspien.fb.domain.TxTrace;
import com.inspien.fb.mapper.TxLogMapper;
import com.inspien.fb.mapper.TxTraceMapper;
import com.inspien.fb.svc.CustMstService;
import com.inspien.fb.svc.FileTelegramManager;
import org.apache.ibatis.jdbc.Null;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.inspien.fb.model.TransferRequest;
import com.inspien.fb.model.TransferResponse;
import com.inspien.fb.svc.FBService;

//2022.07.01 StatementRequest, StatementResponse added
import com.inspien.fb.model.StatementRequest;
import com.inspien.fb.model.StatementResponse;

//2022.07.07 created

import lombok.extern.slf4j.Slf4j;

import static java.time.LocalTime.now;

@Slf4j
@RestController
//@RequestMapping("/mock")
public class FirmAPIController {
	
	// ec2-3-39-156-237.ap-northeast-2.compute.amazonaws.com

	//2022.07.07 created;
	@Autowired
	CustMstService custMstService;

	@Value("${mocklogging.header}") 
	boolean bHeaderLogging;
	@Value("${mocklogging.body}") 
	boolean bBodyLogging;
	@Value("${mocklogging.location}") 
	String location;

	private AtomicLong index = new AtomicLong();
	DecimalFormat intFormatter = new DecimalFormat("000");
	
	private AtomicLong accessCount = new AtomicLong(0);
	private AtomicLong vanAccessCount = new AtomicLong(0);
	private WriteLogs writeLogs = (WriteLogs)ApplicationContextProvider.getBean(WriteLogs.class);

	@Autowired
	ConfigMgmt configMgmt;
	@Autowired
	FileTelegramManager telegramMgr;
	
	@Autowired
	FBService fbSvc;

	@GetMapping("/ping")
	public APIInfo ping() {
		APIInfo info = APIInfo.builder().app("FirmBankingAPI").ver("1.0").timestamp(LocalDateTime.now()).build();
		return info;
	}

	//계좌이체 라우터 => 외부고객 -> 서비스 -> van
	@PostMapping("/firmapi/rt/v1/**")
	public ResponseEntity proxyPost(HttpServletRequest request, @RequestHeader HttpHeaders headers,  @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException {
		LocalDateTime startDateTime = LocalDateTime.now();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		String txIndexFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(startDateTime);
		String custId = custData.get(0).getCustId();

		String uri = request.getRequestURI();
		String method = request.getMethod();
		long size = request.getContentLengthLong();

		if (index.intValue() >= 999) {
			index.set(0);
		}
		String seq = intFormatter.format(index.incrementAndGet());
		String txIndex = txIndexFormat+seq;

		if(log.isInfoEnabled()) {
			//log.debug("{}", new String(body));
			log.debug("accessCount={}, {}, {}, {}", accessCount , method, size, uri);
		}

		Gson gson = new Gson();
		TransferRequest transferReq = gson.fromJson(new String(body), TransferRequest.class);
		TransferResponse response = null;
		List<CustMst> custData = custMstService.getData(transferReq.getOrg_code()); //Connect to mariaDB

		log.info("TransferRequest={},{}", transferReq.getOrg_code(), transferReq);
		writeLogs.insertFileLog(1,1,txIndex,custId,startDateTime,"null","server",String.valueOf(transferReq));

		//log file 작성
		Path p = Paths.get(location, txIndex+".txt");

		if(custData.size() == 1) {
			if (custData.get(0).getInUse().equals("Y")) { //각 고객정보의 InUse 필드를 조회하여 "Y"라면 현재 사용하는 계정이고, "Y"가 아니라면 사용하지 않는 계정이다.
				try {
					log.debug("CustMst List ={}", custData.get(0));
					response = fbSvc.transfer(transferReq);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error("{}", e);
					response = new TransferResponse(500, "9999", e.getMessage());
				}
			} else {
				response = new TransferResponse(401, "1001", "UNUSED_CUSTOMER");
			}
		}
		else if (custData.isEmpty()) {
			response = new TransferResponse(401, "1001", "ORG_CODE_DOES_NOT_EXIST");
		}
		else if (custData.size() > 1) {
			response = new TransferResponse(401, "1001", "ORG_CODE_DUPLICATE_OCCURRENCE");
		}
		LocalDateTime endDateTime = LocalDateTime.now();
		stopWatch.stop();
		writeLogs.insertFileLog(4,1,txIndex,custId,endDateTime,"server","null",String.valueOf(response));
		String reqBody = new String(body);
		String resBody = gson.toJson(response);
		writeLogs.insertDataBaseLog(custId,startDateTime,endDateTime,1,size,stopWatch.getTotalTimeSeconds(),reqBody,resBody,txIndex);
		writeLogs.insertTxTraceLog(startDateTime,custId,telegramMgr.getNowCounter(transferReq.getOrg_code()));

		return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	}

	//거래명세 라우터 => van -> 서비스 -> 고객사
	@PostMapping("/firmapi/rt/v1/bankstatement")
	public ResponseEntity vanGateway(HttpServletRequest request, @RequestHeader HttpHeaders headers,  @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException {
		LocalDateTime startDateTime = LocalDateTime.now();
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		String txIndexFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(startDateTime);
		String custId = custData.get(0).getCustId();
		String uri = request.getRequestURI();
		String method = request.getMethod();
		long size = request.getContentLengthLong();

		if (vanAccessCount.intValue() >= 999) {
			vanAccessCount.set(0);
		}
		String seq = intFormatter.format(vanAccessCount.incrementAndGet());
		String txIndex = txIndexFormat+seq;

		if(log.isInfoEnabled()) {
			log.info("vanAccessCount={}, {}, {}, {}", vanAccessCount , method, size, uri);
		}

		Gson gson = new Gson();
		StatementRequest statementReq = gson.fromJson(new String(body), StatementRequest.class);
		String encData = gson.toJson(new String(body));
		log.info("StatementRequest={},{}", statementReq.getOrg_code(), statementReq);
		List<CustMst> custData = custMstService.getData(statementReq.getOrg_code()); //Connection to mariaDB

		System.out.println("(상세)2-1  고객으로부터 ==> "+request+" | "+statementReq);
		writeLogs.insertFileLog(1,3,txIndex,custId,startDateTime,"van  ","server",String.valueOf(statementReq));


		StatementResponse response = null; //svc.transfer(null);
		String callbackUrl = "";


		log.debug("CustMst = {}", custData);
		if (custData.size() == 1) { //org_code는 유일해야 한다. 따라서 쿼리 결과도 오직 단 한개이다.
			if (custData.get(0).getInUse().equals("Y")) { //각 고객정보의 InUse 필드를 조회하여 "Y"라면 현재 사용하는 계정이고, "Y"가 아니라면 사용하지 않는 계정이다.
				try {
					if (custData.get(0).getCallbackURL() != null) {
						try{
							callbackUrl = custData.get(0).getCallbackURL();
							log.info("callbackurl={}", callbackUrl); //테스트 callbackurl 가져오기
							response = fbSvc.transfer(statementReq, callbackUrl);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							log.error("{}", e);
							response = new StatementResponse(500, "9999", e.getMessage()); //에러코드 메시지를 보기 위한 code
						}
					} else {
						response = new StatementResponse(401, "1001", "CALLBACK_URL_DOES_NOT_EXIST");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error("{}", e);
					response = new StatementResponse(500, "9999", e.getMessage()); //에러코드 메시지를 보기 위한 code
				}
			}
			else {
				response = new StatementResponse(401, "1001", "UNUSED_CUSTOMER");
			}
		}
		else if (custData.isEmpty()) {
			response = new StatementResponse(401, "1001", "ORG_CODE_DOES_NOT_EXIST"); //org_code로 쿼리하였을떄, 결과값이 없다면 에러
		}
		else if (custData.size() > 1) {
			response = new StatementResponse(401, "1001", "ORG_CODE_DUPLICATE_OCCURRENCE"); //org_code로 쿼리하였을떄, 결과값이 여러개라면 에러
		}
		log.info("bankStatement response ==> {}",response);
		LocalDateTime endDateTime = LocalDateTime.now();
		stopWatch.stop();
		writeLogs.insertFileLog(4,3,txIndex,custId,endDateTime,"server","van  ",String.valueOf(response));

		String reqBody = new String(body);
		String resBody = gson.toJson(response);
		writeLogs.insertDataBaseLog(custId,startDateTime,endDateTime,3,size,stopWatch.getTotalTimeSeconds(),reqBody,resBody,txIndex);
		return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	}

	//콜백url테스트를 위한 라우터
	@PostMapping ("/bankstatement/test") //거래명세 CallBackURL 테스팅을 위한 라우터
	public ResponseEntity externalCust(HttpServletRequest request, @RequestHeader HttpHeaders headers,  @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException {
		String today = DateTimeFormatter.ofPattern("yyyyMMddmmss").format(ZonedDateTime.now());
		Gson gson = new Gson();
		StatementRequest statementReq = gson.fromJson(new String(body), StatementRequest.class);
		log.info("StatementRequest={},{}", statementReq.getOrg_code(), statementReq);
		APIInfo info = APIInfo.builder().app("FirmBankingAPI").ver("1.0").timestamp(LocalDateTime.now()).build();
		StatementResponse response = new StatementResponse();
		response.setStatus(200);
		response.setRequest_at(today);
		return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	}

	//cache 테스트를 위한 라우터
	@PutMapping("/CustMst/update/{id}") //DB update 라우터
	public void dbUpdate(@PathVariable String id, @RequestBody(required = false) byte[] body) {
		Gson gson = new Gson();
		CustMst custMst = gson.fromJson(new String(body), CustMst.class);
		custMst.setOrgCd(id);
		log.info("CustMst = {}", new String(body));
		log.info("updateResult = {}", custMstService.updateData(custMst));
	}


}
