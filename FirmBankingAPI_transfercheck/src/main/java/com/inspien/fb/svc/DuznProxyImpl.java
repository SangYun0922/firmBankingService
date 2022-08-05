package com.inspien.fb.svc;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import com.inspien.fb.model.*;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

//2020.07.01 StatementRequest, StatementResponse added


public class DuznProxyImpl extends VANProxy{

	@Value("${van.duzn.test.open}")
	static public String TEST_OPEN_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/account/open";
	@Value("${van.duzn.test.transfer}")
	static public String TEST_TRANSFER_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/transfer";
	@Value("${van.duzn.test.transfer-check}")
	static public String TEST_TRANSFER_CHECK_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/transfer/check";

	@Value("${van.duzn.prd.open}")
	static public String PROD_OPEN_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/account/open";

	@Value("${van.duzn.prd.transfer}")
	static public String PROD_TRANSFER_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/transfer";

	@Value("${van.duzn.prd.transfer-check}")
	static public String PROD_TRANSFER_CHECK_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/transfer/check";

	
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

	//2020.07.01 transfer added
	public StatementResponse transfer(StatementRequest req, String callbackURL) throws ParseException, URISyntaxException, IOException {
		ObjectMapper om = new ObjectMapper();
		String reqJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(req);

		String targetUrl = callbackURL;
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");

		String responseBody = callAPIPost(targetUrl, reqJson, headers);
		StatementResponse response = om.readValue(responseBody, StatementResponse.class) ;

		return response;
	}

	//(Only use TransferCheck)
	public TransferCheckResponse transfer(TransferCheckRequest req) throws IOException, URISyntaxException, ParseException {
		ObjectMapper om = new ObjectMapper();
		String reqJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(req);

		String targetUrl = TEST_TRANSFER_CHECK_URL;
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");

		String responseBody = callAPIPost(targetUrl, reqJson, headers);
		System.out.println("responseBody = " + responseBody);
		TransferCheckResponse response = om.readValue(responseBody, TransferCheckResponse.class);

		return response;
	}
}
