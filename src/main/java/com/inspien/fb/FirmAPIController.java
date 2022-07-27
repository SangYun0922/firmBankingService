package com.inspien.fb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.inspien.fb.domain.CustMst;
import com.inspien.fb.domain.TxLog;
import com.inspien.fb.mapper.TxLogMapper;
import com.inspien.fb.svc.CustMstService;
import com.inspien.fb.svc.FileTelegramManager;
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
	
	private AtomicInteger index = new AtomicInteger();
	DecimalFormat intFormatter = new DecimalFormat("000");
	
	private AtomicLong accessCount = new AtomicLong(0);
	private AtomicLong vanAccessCount = new AtomicLong(0);
	@Autowired
	private TxLogMapper txLogMapper;
	@Autowired
	private FileTelegramManager telegramMgr;


	@Autowired
	ConfigMgmt configMgmt;
	
	@Autowired
	FBService fbSvc;
	
	//Map<String , Map<String, AtomicLong> > custCounter = new HashMap<String , Map<String, AtomicLong>>();
	
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
		String dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(startDateTime);
		System.out.println(startDateTime);

		if (this.index.intValue() >= 999) {
			this.index.set(0);
		}
		String seq = intFormatter.format(this.index.incrementAndGet());
		System.out.println(seq);
		String txIndex = txIndexFormat + seq;

		long count = accessCount.incrementAndGet();
		String uri = request.getRequestURI();
		String method = request.getMethod();
		long size = request.getContentLengthLong();

		if(log.isInfoEnabled()) {
			//log.debug("{}", new String(body));
			log.debug("accessCount={}, {}, {}, {}", count , method, size, uri);
		}
//		writeLog(request, headers, body);

		Gson gson = new Gson();
		TransferRequest transferReq = gson.fromJson(new String(body), TransferRequest.class);
		log.info("TransferRequest={},{}", transferReq.getOrg_code(), transferReq);

		System.out.println("(상세)1-1  고객으로부터 ==> "+request+" | "+transferReq);
		JsonObject txLogByJson = new JsonObject();
		TxLog txLog = null;

		txLogByJson.addProperty("TxIdx",txIndex);
		txLogByJson.addProperty("TelegramNo",transferReq.getTelegram_no());
//		txLogByJson.addProperty("TxType",);
		txLogByJson.addProperty("BankCd",transferReq.getRv_bank_code());
		txLogByJson.addProperty("Size",size);
		txLogByJson.addProperty("TxDate", dateFormat);
		txLogByJson.addProperty("StartDT", String.valueOf((startDateTime)) + ZoneId.of("+09:00"));
		long txNo = telegramMgr.getNowCounter(transferReq.getOrg_code());
		txLogByJson.addProperty("StmtCnt", txNo);


		//FBService svc = new FBService();
		TransferResponse response = null;

		List<CustMst> custData = custMstService.getData(transferReq.getOrg_code()); //Connect to mariaDB
		txLogByJson.addProperty("CustId",custData.get(0).getCustId());

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
		System.out.println("(상세)1-4  고객에게 ==> "+response+" | "+gson.toJson(response));
		LocalDateTime endDateTime = LocalDateTime.now();
		stopWatch.stop();
		System.out.println(response.getStatus());
		if (Objects.equals(response.getStatus(), 200)){
			txLogByJson.addProperty("Status","T");
		}else{
			txLogByJson.addProperty("Status","F");
		}
		txLogByJson.addProperty("EndDT", String.valueOf((endDateTime)) + ZoneId.of("+09:00"));
		txLogByJson.addProperty("RoundTrip",stopWatch.getTotalTimeSeconds());
		System.out.println(txLogByJson);

		//json을 TxLog클래스로 변환
		txLog = gson.fromJson(txLogByJson,TxLog.class);
		System.out.println(txLog.getTxDate());
		txLogMapper.logAdd(txLog);
		return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	}

	//거래명세 라우터 => van -> 서비스 -> 고객사
	@PostMapping("/firmapi/rt/v1/bankstatement")
	public ResponseEntity vanGateway(HttpServletRequest request, @RequestHeader HttpHeaders headers,  @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException {
		long count = vanAccessCount.incrementAndGet();

		String uri = request.getRequestURI();
		String method = request.getMethod();
		long size = request.getContentLengthLong();

		if(log.isInfoEnabled()) {
			log.info("vanAccessCount={}, {}, {}, {}", count , method, size, uri);
		}

		Gson gson = new Gson();
		StatementRequest statementReq = gson.fromJson(new String(body), StatementRequest.class);

		log.info("StatementRequest={},{}", statementReq.getOrg_code(), statementReq);

//		writeLog(request, headers, body);

		//FBService svc = new FBService();
		StatementResponse response = null; //svc.transfer(null);
		String callbackUrl = "";

		List<CustMst> custData = custMstService.getData(statementReq.getOrg_code()); //Connection to mariaDB

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

//	private void writeLog(HttpServletRequest request, HttpHeaders headers, byte[] body) {
//		LocalDateTime dateTime = LocalDateTime.now();
//        String timeStamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(dateTime);
//		if (this.index.intValue() >= 999) {
//			this.index.set(0);
//		}
//		String seq = intFormatter.format(this.index.incrementAndGet());
//
//		Path p = Paths.get(location, timeStamp+seq+".txt");
//		try {
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//			if(bHeaderLogging && headers != null) {
//
//				headers.forEach((key, value) -> {
//					try {
//						bos.write(String.format(
//						  "%s = %s" + System.lineSeparator(), key, value.stream().collect(Collectors.joining("|"))).getBytes());
//					} catch (IOException e) {
//					}
//			    });
//				bos.write(System.lineSeparator().getBytes());
//
//			}
//			if(bBodyLogging && body != null) {
//				bos.write(body);
//				Enumeration params = request.getParameterNames();
//				while(params.hasMoreElements()) {
//					String name = (String) params.nextElement();
//					System.out.println(name + " : " + request.getParameter(name) + "     ");
//				}
//
//			}
//			Files.write(p, bos.toByteArray(), StandardOpenOption.CREATE);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

//	private TxLog insertLog() {
//		TxLog txLog = new TxLog();
//		LocalDateTime dateTime = LocalDateTime.now();
//		String timeStamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(dateTime);
//		String sqlDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateTime);
//		if (this.index.intValue() >= 999) {
//			this.index.set(0);
//		}
//		String seq = intFormatter.format(this.index.incrementAndGet());
//		txLog.setTxIdx(timeStamp+seq);
//		txLog.setTxDate(Date.valueOf(sqlDateFormat));
//		txLog.setStartDT(Timestamp.valueOf(dateTime));
//		return txLog;
//	}
}
