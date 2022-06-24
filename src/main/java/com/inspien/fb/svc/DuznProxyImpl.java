package com.inspien.fb.svc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspien.fb.model.OpenRequest;
import com.inspien.fb.model.OpenResponse;
import com.inspien.fb.model.TransferRequest;
import com.inspien.fb.model.TransferResponse;

public class DuznProxyImpl extends VANProxy{

	//application.yaml에 기재된 속성에 대한 정보로 값을 초기화
	@Value("${van.duzn.test.open}") 
	static public String TEST_OPEN_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/account/open";
	
	@Value("${van.duzn.test.transfer}") 
	static public String TEST_TRANSFER_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/transfer";

	@Value("${van.duzn.prd.open}") 
	static public String PROD_OPEN_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/account/open";
	
	@Value("${van.duzn.prd.transfer}") 
	static public String PROD_TRANSFER_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/transfer";

	
	public OpenResponse open(OpenRequest req) throws ParseException, URISyntaxException, IOException { //게시전문을 파라미터로 받아서
		ObjectMapper om = new ObjectMapper(); //ObjectMapper란 JSON값을 더 잘 활용할 수 있게 해주는 라이브러리이다.
		String reqJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(req); //req에 있는 멤버변수 데이터들을 String으로 변환하고
		// json데이터를 예쁘게 정렬하여 reqJson에 담아준다.

		String targetUrl = TEST_OPEN_URL; //targetUrl에 "https://test-gw-firm.dozn.co.kr/api/rt/v1/account/open"를 담아둔다.
		Map<String, String> headers = new HashMap<String, String>(); //headers라는 해쉬맵을 만든다.
		headers.put("Content-Type", "application/json");//이때, headers를 출력하면, {Content-Type=application/json}으로 출력된다.
		
		String responseBody = callAPIPost(targetUrl, reqJson, headers);//callAPIPost를 실행하여, vanProxy에 데이터를 전달한다.
		//van으로 부터 온 리턴값은, 고객이 요청한 거래요청에 대한 van의 json응답이다. 이 응답을 responsebody에 넣어준다.
		OpenResponse response = om.readValue(responseBody, OpenResponse.class); //responsebody에 있는 데이터값을 추출하여,
		//OpenResponse객체인 response에 매핑한다.
		/* 총 매핑되는 속성은 아래와 같다.
		*  status
		*  drw_bank_code
		*  open_state
		*  error_code
		*  error_message
		* */
		return response; //그후 매핑된 response를 반환한다.
	}
	public TransferResponse transfer(TransferRequest req) throws ParseException, URISyntaxException, IOException {
		ObjectMapper om = new ObjectMapper();
		String reqJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(req);

		String targetUrl = TEST_TRANSFER_URL;
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json"); //open함수와 로직은 동일하다 단 URL만 다를뿐
		
		String responseBody = callAPIPost(targetUrl, reqJson, headers);
		TransferResponse response = om.readValue(responseBody, TransferResponse.class) ;
		
		return response; //FBService.java의 88line으로 리스폰스
	}
}
