package com.inspien.fb.svc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContexts;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.inspien.fb.model.OpenRequest;
import com.inspien.fb.model.OpenResponse;
import com.inspien.fb.model.TransferRequest;
import com.inspien.fb.model.TransferResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class VANProxy {
	final static public String OPEN_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/account/open";
	final static public String TRANSFER_URL = "https://test-gw-firm.dozn.co.kr/api/rt/v1/transfer";
	
	
	MediaType mediaType = MediaType.APPLICATION_JSON;
	int timeOutSecond = 30;

	CloseableHttpClient httpClient = null;
	KeyStore trustKeyStore;
	HttpClientConnectionManager cm = null;
	
	public void init(String trustKeyStorePath, String trustKeyStorePassword) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException {
		if(trustKeyStorePath != null) {
			trustKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustKeyStore.load(new FileInputStream(new File(trustKeyStorePath)), trustKeyStorePassword.toCharArray());
		}

		final SSLContext sslcontext = SSLContexts.custom()
				.loadTrustMaterial(null, new TrustSelfSignedStrategy())
				//.loadKeyMaterial(accKeyStore, accKeyStorePassword.toCharArray())
				.build();
		
		// Allow TLSv1.2 protocol only
		final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
				.setSslContext(sslcontext)
				.setTlsVersions(TLS.V_1_2, TLS.V_1_3)
				.build();
		cm = PoolingHttpClientConnectionManagerBuilder.create()
				.setSSLSocketFactory(sslSocketFactory).build();

	}
	protected String callAPIPost(String url, String req, Map<String, String> headers) throws URISyntaxException, IOException, ParseException {
		String responseBody = null; //응답데이터를 기록하기 위한 변수
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		System.out.println("!@#$httpClient = " + HttpClients.custom()); //게시전문을 확인할때는 null ok, 그러나, 왜 transfer를 할때는, 왜 값이 들어오게 되는가?

        final HttpPost httppost = new HttpPost(url);

    	for (Map.Entry<String, String> entry : headers.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
        	httppost.setHeader(key, val);
		} // 헤더에 있는 데이터를 가져와 loop를 돌면서 httppost 변수의 헤더에 부착
        //httppost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        //httppost.setHeader("Accept", "application/json");
        
        // set requestbody json을 받아서 setEntity로 httppost 변수의 body에 데이터 전달.
        httppost.setEntity(new StringEntity(req));
        
    	log.debug("----------------------------------------");
        log.debug("Executing request {} {} ==> {}" , httppost.getMethod() , httppost.getUri(), req);
		//httppost.getMethod()를 하게 되면, POST가 추출되고, httppost.geturi()를 하게되면, final HttpPost httppost = new HttpPost(url);주었던 url이 추출된다.
		//그후 openRequest객체의 속성값을 출력한다.
        final HttpClientContext clientContext = HttpClientContext.create(); //clientContext
        try (CloseableHttpResponse response = httpClient.execute(httppost, clientContext)) {
            responseBody = EntityUtils.toString(response.getEntity());
        	log.debug("----------------------------------------");
        	log.debug("{} {} ==> {}", response.getCode(), response.getReasonPhrase(), responseBody);

            final SSLSession sslSession = clientContext.getSSLSession();
            if (sslSession != null) {
            	log.debug("SSL protocol {}, cipher suite {}", sslSession.getProtocol(), sslSession.getCipherSuite());
            }
        }
        finally {
        }
        
		return responseBody; // "status","error_code","error_message", 등등이 json형식으로 리턴된다.
	}
	protected String callAPIGet(String url, String req, Map<String, String> headers) throws URISyntaxException, IOException, ParseException {
		String responseBody = null;
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		
        final HttpGet httppost = new HttpGet(url);
        httppost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        httppost.setHeader("Accept", "application/json");
        
        // set requestbody
        //httppost.setEntity(new StringEntity(req));
        
    	log.debug("----------------------------------------");
        log.debug("Executing request {} {} ==> {}" , httppost.getMethod() , httppost.getUri(), req);

        final HttpClientContext clientContext = HttpClientContext.create();
        try (CloseableHttpResponse response = httpClient.execute(httppost, clientContext)) {
            
            responseBody = EntityUtils.toString(response.getEntity());
        	log.debug("----------------------------------------");
        	log.debug("{} {} ==> {}", response.getCode(), response.getReasonPhrase(), responseBody);

            final SSLSession sslSession = clientContext.getSSLSession();
            if (sslSession != null) {
            	log.debug("SSL protocol {}, cipher suite {}", sslSession.getProtocol(), sslSession.getCipherSuite());
            }
        }
        finally {
        }
        
		return responseBody;
	}
	
	abstract public OpenResponse open(OpenRequest req) throws Exception; //DuznProxyImpl에서 구현
	abstract public TransferResponse transfer(TransferRequest req) throws Exception; //DuznProxyImpl에서 구현
}
