package com.inspien.fb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.inspien.fb.domain.CustMst;
import com.inspien.fb.svc.CustMstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;
import com.inspien.fb.model.TransferRequest;
import com.inspien.fb.model.TransferResponse;
import com.inspien.fb.svc.FBService;

//2022.07.01 StatementRequest, StatementResponse added
import com.inspien.fb.model.StatementRequest;
import com.inspien.fb.model.StatementResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class FirmAPIController {
	
	// ec2-3-39-156-237.ap-northeast-2.compute.amazonaws.com

	//2022.07.07 created;
	private CustMstService custMstService;
	public FirmAPIController(CustMstService custMstService) {
		this.custMstService = custMstService;
	}

//	@Value("${mocklogging.header}")
	boolean bHeaderLogging = true; //true
//	@Value("${mocklogging.body}")
	boolean bBodyLogging = true; // true
//	@Value("${mocklogging.location}")
	String location = "./logs"; // ./logs
	
	private AtomicInteger index = new AtomicInteger();
	DecimalFormat intFormatter = new DecimalFormat("000");
	
	private AtomicLong accessCount = new AtomicLong(0);
	private AtomicLong vanAccessCount = new AtomicLong(0);
	
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


	@PostMapping("/firmapi/rt/v1/**")
	public ResponseEntity proxyPost(HttpServletRequest request, @RequestHeader HttpHeaders headers,  @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException {
		long count = accessCount.incrementAndGet();

		String uri = request.getRequestURI();
		String method = request.getMethod();
		long size = request.getContentLengthLong();

		if(log.isInfoEnabled()) {
			//log.debug("{}", new String(body));
			log.debug("accessCount={}, {}, {}, {}", count , method, size, uri);
		}

		writeLog(request, headers, body);

		Gson gson = new Gson();
		TransferRequest transferReq = gson.fromJson(new String(body), TransferRequest.class);
		
		log.info("TransferRequest={},{}", transferReq.getOrg_code(), transferReq);
		
		//FBService svc = new FBService();
		TransferResponse response = null;

		List<CustMst> custData = custMstService.getData(transferReq.getOrg_code()); //Connect to mariaDB
		log.info("CustMst List ={}", custData.get(0));
		if(custData.size() == 1) {
			if (custData.get(0).getInUse().equals("Y")) { //각 고객정보의 InUse 필드를 조회하여 "Y"라면 현재 사용하는 계정이고, "Y"가 아니라면 사용하지 않는 계정이다.
				try {
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
		return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	}

	//2022.07.08 update
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

		writeLog(request, headers, body);

		//FBService svc = new FBService();
		StatementResponse response = null; //svc.transfer(null);
		String callbackUrl = "";

		List<CustMst> custData = custMstService.getData(statementReq.getOrg_code()); //Connection to mariaDB

		log.info("CustMst = {}", custData);
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

	@PutMapping("/CustMst/update/{id}") //DB update 라우터
	public void dbUpdate(@PathVariable String id, @RequestBody(required = false) byte[] body) {
		Gson gson = new Gson();
		CustMst custMst = gson.fromJson(new String(body), CustMst.class);
		custMst.setOrgCd(id);
		log.info("CustMst = {}", new String(body));
		log.info("updateResult = {}", custMstService.updateData(custMst));
	}

	@PostMapping("/CachingTest") //CacheTest 라우터 (cache의 경우)
	public void Cached(@RequestBody(required = false) byte[] body) throws IOException, URISyntaxException {
		Gson gson = new Gson();
		TransferRequest transferReq = gson.fromJson(new String(body), TransferRequest.class);
		log.info("TransferRequest={},{}", transferReq.getOrg_code(), transferReq);
		List<CustMst> custData = custMstService.getData(transferReq.getOrg_code()); //Connect to mariaDB
		log.info("CustMst = {}", custData);
	}

	private void writeLog(HttpServletRequest request, HttpHeaders headers, byte[] body) {
		LocalDateTime dateTime = LocalDateTime.now();
        String timeStamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(dateTime);
		if (this.index.intValue() >= 999) {
			this.index.set(0);
		}
		String seq = intFormatter.format(this.index.incrementAndGet());
		
		Path p = Paths.get(location, timeStamp+seq+".txt");
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			if(bHeaderLogging && headers != null) {
				
				headers.forEach((key, value) -> {
					try {
						bos.write(String.format(
						  "%s = %s" + System.lineSeparator(), key, value.stream().collect(Collectors.joining("|"))).getBytes());
					} catch (IOException e) {
					}
			    });
				bos.write(System.lineSeparator().getBytes());
				
			}
			if(bBodyLogging && body != null) {
				bos.write(body);
			}
			Files.write(p, bos.toByteArray(), StandardOpenOption.CREATE);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
