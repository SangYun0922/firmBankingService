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

	@Value("${van.duzn.test.open}") 
	static public String TEST_OPEN_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/account/open";
	
	@Value("${van.duzn.test.transfer}") 
	static public String TEST_TRANSFER_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/transfer";

	@Value("${van.duzn.prd.open}") 
	static public String PROD_OPEN_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/account/open";
	
	@Value("${van.duzn.prd.transfer}") 
	static public String PROD_TRANSFER_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/transfer";

	
	public OpenResponse open(OpenRequest req) throws ParseException, URISyntaxException, IOException {
		ObjectMapper om = new ObjectMapper();
		String reqJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(req);

		String targetUrl = TEST_OPEN_URL;
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		
		String responseBody = callAPIPost(targetUrl, reqJson, headers);
		OpenResponse response = om.readValue(responseBody, OpenResponse.class) ;
		
		return response;
	}
	public TransferResponse transfer(TransferRequest req) throws ParseException, URISyntaxException, IOException {
		ObjectMapper om = new ObjectMapper();
		String reqJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(req);

		String targetUrl = TEST_TRANSFER_URL;
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		
		String responseBody = callAPIPost(targetUrl, reqJson, headers);
		TransferResponse response = om.readValue(responseBody, TransferResponse.class) ;
		
		return response;
	}
}
