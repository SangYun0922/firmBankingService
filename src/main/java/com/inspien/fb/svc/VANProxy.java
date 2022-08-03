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
import java.time.LocalDateTime;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import com.google.gson.Gson;
import com.inspien.fb.ApplicationContextProvider;
import com.inspien.fb.WriteLogs;
import com.inspien.fb.model.*;
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

//2022.07.01 StatementRequest, StatementResponse added

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

	private WriteLogs writeLogs = (WriteLogs) ApplicationContextProvider.getBean(WriteLogs.class);
	
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
		String responseBody = null;
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		
        final HttpPost httppost = new HttpPost(url); //url = callback URL
        
    	for (Map.Entry<String, String> entry : headers.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
        	httppost.setHeader(key, val);
		}
        //httppost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
        //httppost.setHeader("Accept", "application/json");
        
        // set requestbody
        httppost.setEntity(new StringEntity(req));
		Gson gson = new Gson();
        
    	log.debug("----------------------------------------");
        log.debug("Executing request {} {} ==> {}" , httppost.getMethod() , httppost.getUri(), req);

        final HttpClientContext clientContext = HttpClientContext.create();
		int txType = 0;
		String to = "";
		String from = "";
		if (httppost.getUri().getPath().contains("transfer")){
			txType = 1;
			to = "server";
			from = "van  ";
		}else if(httppost.getUri().getPath().contains("bankstatement")){
			txType = 3;
			to = "server";
			from = "null";
		}

		writeLogs.insertFileLog(2,txType,"null","null", LocalDateTime.now(),to,from,gson.toJson(req));
        try (CloseableHttpResponse response = httpClient.execute(httppost, clientContext)) {
            responseBody = EntityUtils.toString(response.getEntity());
			writeLogs.insertFileLog(3,txType,"null","null", LocalDateTime.now(),from,to,responseBody);

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
	
	abstract public OpenResponse open(OpenRequest req) throws Exception;
	abstract public TransferResponse transfer(TransferRequest req) throws Exception;

	//2022.07.01 transfer added
	abstract public StatementResponse transfer(StatementRequest req, String callbackURL) throws Exception;
	abstract public TransferCheckResponse transfer(TransferCheckRequest req) throws Exception;
}
