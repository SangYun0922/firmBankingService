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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
//@RequestMapping("/mock")
public class FirmAPIController {
	
	// ec2-3-39-156-237.ap-northeast-2.compute.amazonaws.com
	// mocklogging은 yaml파일에 정의되어있다. 따라서, 해당 파일의 속성을 각 변수의 초기값으로 설정한다.
	@Value("${mocklogging.header}") 
	boolean bHeaderLogging; //mocklogging : header = true
	@Value("${mocklogging.body}") 
	boolean bBodyLogging; //mocklogging : body = true
	@Value("${mocklogging.location}")
	String location; //mocklogging : location = ./logs
	
	private AtomicInteger index = new AtomicInteger();
	DecimalFormat intFormatter = new DecimalFormat("000");
	
	private AtomicLong accessCount = new AtomicLong(0);
	private AtomicLong vanAccessCount = new AtomicLong(0);
	
	@Autowired
	ConfigMgmt configMgmt;
	
	//Map<String , Map<String, AtomicLong> > custCounter = new HashMap<String , Map<String, AtomicLong>>();
	
	@GetMapping("/ping")
	public APIInfo ping() {
		//builder 패턴으로 APIInfo에 "FirmBankingAPI"라는 이름과 "1.0"버전, 현재시간 타임스태프를 멤버변수로 넣어주고 build하여 객체를 만든다.
		APIInfo info = APIInfo.builder().app("FirmBankingAPI").ver("1.0").timestamp(LocalDateTime.now()).build();
		//생성된 객체를 리턴한다. 어디로? 화면으로!
		return info;
	}


	@PostMapping("/firmapi/rt/v1/**")
	//HttpServletRequest로 선언된 변수에 post값의 데이터가 바인딩 된다.
	//바인딩 된 값 각각을 가져올때는, getparameter를 사용할 수 있다.
	//@requestheader HTTP header의 내용을 변수로 바인딩 해준다.
	//@requestbody  클라이언트가 전송하는 Json(application/json) 형태의 HTTP Body 내용을 Java Object로 변환시켜주는 역할을 한다.
	//그렇기 때문에 Body가 존재하지 않는 HTTP Get 메소드에 @RequestBody를 활용하려고 한다면 에러가 발생하게 된다. 또한, 반드시 필요한 값이 아니라서 required=false로 지정하고,
	//HTTP Body가 byte[] body 로 바인딩된다.
	public ResponseEntity proxyPost(HttpServletRequest request, @RequestHeader HttpHeaders headers,  @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException {
		long count = accessCount.incrementAndGet();
		System.out.println("count = " + count);
		System.out.println("request = " + request);
		System.out.println("headers = " + headers);

		String uri = request.getRequestURI(); //uri = /firmapi/rt/v1/
		String method = request.getMethod(); //method = POST

		long size = request.getContentLengthLong();
		System.out.println("size = " + size);

		if(log.isInfoEnabled()) {
			//log.debug("{}", new String(body));
			log.debug("accessCount={}, {}, {}, {}", count , method, size, uri); //터미널 상에 로그를 띄워준다.
		}

		writeLog(request, headers, body); //log를 파일로써 기록하는 함수
		
		log.debug("cust={}", configMgmt.getCustomers()); //application.yaml에 세팅되어있는 master값을 configmgmt 클래스 객체로 바인딩시킨다. 그후, Customer해쉬맵을 출력한다.
		Gson gson = new Gson(); //Gson이 무엇인가? 왜 gson으로 바꾸어주는가?

		TransferRequest transferReq = gson.fromJson(new String(body), TransferRequest.class); //request body를 받아서 이것을 json으로 부터 gson으로 바꿔준뒤, transferreq라는 객체에 담아준다.
		
		log.info("TransferRequest={},{}", transferReq.getOrg_code(), transferReq); //담겨진 transfer객체에서 기관번호와 transfer객체 전체를 출력한다.
		
		FBService svc = new FBService(); //FBService객체를 생성한다.

		TransferResponse response = null;
		
		// if customer is valid
		// containKey란? => 인자로 보낸 키가 있으면 true 없으면 false를 반환한다.
		// configMgmt에 post요청의 org_code가 포함되어있으면 아래코드를 실행시킨다.
		if(configMgmt.getCustomers().containsKey( transferReq.getOrg_code() )) {
			try {
				response = svc.transfer(transferReq); //transferreq 객체를 인자로 svc클래스의 transfer 멤버함수를 실행하여 리턴값을 response에 담는다.턴
				//즉 게시전문 쿼리, transfer요청이 전부 이뤄지고 받은 응답요청이 여기에 들어간다.
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("{}", e);
				response = new TransferResponse(500, "9999", e.getMessage());
			}
		}
		else { //만약 configMgmt에 org_code가 등록되지 않았다면, 아래 코드를 실행한다.
			response = new TransferResponse(401, "1001", "no customer code found.");

		}
		return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK); //최종적으로 response를 받은것을 json화 시키고, httpStatus를 ok(200)으로 설정한뒤 리턴한다.
	}
	
	@PostMapping("/firmapi/rt/v1/bankstatement")
	public ResponseEntity vanGateway(HttpServletRequest request, @RequestHeader HttpHeaders headers,  @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException {
		long count = vanAccessCount.incrementAndGet();

		String uri = request.getRequestURI();
		String method = request.getMethod();
		long size = request.getContentLengthLong();

		if(log.isInfoEnabled()) {
			//log.debug("{}", new String(body));
			log.info("vanAccessCount={}, {}, {}, {}", count , method, size, uri);
		}

		writeLog(request, headers, body); //log를 파일로써 기록하는 함수
		
		FBService svc = new FBService();
		TransferResponse response = null; //svc.transfer(null);
		
		Gson gson = new Gson();
		
		return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	}
	
	
	
	private void writeLog(HttpServletRequest request, HttpHeaders headers, byte[] body) {
		LocalDateTime dateTime = LocalDateTime.now();
        String timeStamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(dateTime);
		if (this.index.intValue() >= 999) { //private AtomicInteger index = new AtomicInteger();로 해당 클래스에서 선언된 멤버변수를 의미
			this.index.set(0);
		}
		String seq = intFormatter.format(this.index.incrementAndGet());
		//인덱스값을 +1한뒤 가져온다 그리고 그것에 대하여 DecimalFormat intFormatter = new DecimalFormat("000");로 선언된 객체화 시킨다.
		//그것을 seq라고 정의, 따라서, 001, 010, 990과 같은 세자리 포맷을 가지게 된다.
		
		Path p = Paths.get(location, timeStamp+seq+".txt"); //location + timeStamp + seq + .txt 로 p라는 파일의 경로를 저장한다.
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); //내부저장공간에 데이터를 적재하기 위하여 ByteArrayOutputStream() 객체를 생성한다.

			if(bHeaderLogging && headers != null) { // requestHeader가 null이 아니고, bHeaderLogging이 true라면,
				// header의 형식은 아래와 같다.
				// [content-type:"application/json", user-agent:"PostmanRuntime/7.29.0", host:"localhost:9000" ...]
				// key-value 쌍 리스트로 이루어져 있으므로, forEach를 통하여 loop돌리기 가능
				headers.forEach((key, value) -> {
					try {
						bos.write(String.format( //byteArrayOutputStream에 헤더의 정보를 써준다.
						  "%s = %s" + System.lineSeparator(), key, value.stream().collect(Collectors.joining("|"))).getBytes());
					} catch (IOException e) {
					}
			    });
				//getBytes()이란? getBytes() 메서드는 String(문자열)을 default charset으로 인코딩하여 byte 배열로 반환해준다.
				bos.write(System.lineSeparator().getBytes()); //따라서 tab기능으로 한줄이 띄워진다.

			}
			if(bBodyLogging && body != null) { //body부분에 데이터가 있고, bBodyLogging이 true라면
				bos.write(body); //byteArrayOutputStream에 body의 내용을 기입한다. header와 다른 이유는 바디의 데이터는 json파일 형식으로 오기 때문이다.
			}
			Files.write(p, bos.toByteArray(), StandardOpenOption.CREATE); // 그후, byteArrayOutputStream에 담긴것을 파일의 형태로 write하여 기록한다.

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
