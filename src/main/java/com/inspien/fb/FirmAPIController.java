package com.inspien.fb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.inspien.fb.mapper.SampleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
//@RequestMapping("/mock")
public class FirmAPIController {
	
	// ec2-3-39-156-237.ap-northeast-2.compute.amazonaws.com

	// 2022.07.05 SampleMapper 추가;
	@Autowired
	SampleMapper sampleMapper;
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
		
		log.debug("cust={}", configMgmt.getCustomers());
		
		Gson gson = new Gson();
		TransferRequest transferReq = gson.fromJson(new String(body), TransferRequest.class);
		
		log.info("TransferRequest={},{}", transferReq.getOrg_code(), transferReq);
		
		//FBService svc = new FBService();
		TransferResponse response = null;
		
		// if customer is valid
		if(configMgmt.getCustomers().containsKey( transferReq.getOrg_code() )) {
			try {
				response = fbSvc.transfer(transferReq);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("{}", e);
				response = new TransferResponse(500, "9999", e.getMessage());
			}
		}
		else {
			response = new TransferResponse(401, "1001", "no customer code found.");
		}
		
		
		return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	}

	//2022.07.01 update
	@PostMapping("/firmapi/rt/v1/bankstatement")
	public ResponseEntity vanGateway(HttpServletRequest request, @RequestHeader HttpHeaders headers,  @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException {

		// 2022.07.05 테스트코드 추가;
		System.out.println(sampleMapper.selectSampleData());
		
		long count = vanAccessCount.incrementAndGet();

		String uri = request.getRequestURI();
		String method = request.getMethod();
		long size = request.getContentLengthLong();

		if(log.isInfoEnabled()) {
			//log.debug("{}", new String(body));
			log.info("vanAccessCount={}, {}, {}, {}", count , method, size, uri);
		}

		log.debug("cust={}", configMgmt.getCustomers());

		Gson gson = new Gson();
		StatementRequest statementReq = gson.fromJson(new String(body), StatementRequest.class);

		log.info("StatementRequest={},{}", statementReq.getOrg_code(), statementReq);

		writeLog(request, headers, body);

		//FBService svc = new FBService();
		StatementResponse response = null; //svc.transfer(null);
		String callbackUrl = "";

		if(configMgmt.getCustomers().containsKey( statementReq.getOrg_code() )) {
			try {
				log.info("statememtReq.org_code={}",statementReq.getOrg_code());
				if (configMgmt.getCustomers().get(statementReq.getOrg_code()).containsKey("callback")) {
					try {
						callbackUrl = configMgmt.getCustomers().get(statementReq.getOrg_code()).get("callback");
						response = fbSvc.transfer(statementReq, callbackUrl);
						log.info("callbackurl={}", callbackUrl); //테스트 callbackurl 가져오기
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						log.error("{}", e);
						response = new StatementResponse(500, "9999", e.getMessage()); //에러코드 메시지를 보기 위한 code
					}
				} else {
					response = new StatementResponse(401, "1001","no callback url code found.");
					System.out.println("response = " + response);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("{}", e);
				response = new StatementResponse(401, "1001", e.getMessage()); //에러코드 메시지를 보기 위한 code
			}
		} else {
			response = new StatementResponse(401, "1001", "no customer code found.");
		}
		return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	}

	@PostMapping ("/bankstatement/test") //테스팅을 위한 라우터
	public ResponseEntity externalcust(HttpServletRequest request, @RequestHeader HttpHeaders headers,  @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException {
		String today = DateTimeFormatter.ofPattern("yyyyMMddmmss").format(ZonedDateTime.now());
		System.out.println("today = " + today);
		Gson gson = new Gson();
		StatementRequest statementReq = gson.fromJson(new String(body), StatementRequest.class);
		log.info("StatementRequest={},{}", statementReq.getOrg_code(), statementReq);
		APIInfo info = APIInfo.builder().app("FirmBankingAPI").ver("1.0").timestamp(LocalDateTime.now()).build();
		StatementResponse response = new StatementResponse();
		response.setStatus(200);
		response.setRequest_at(today);
		return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
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
