package com.inspien.fb.svc;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
@Component
@Data
public class GetClient {
    HttpClientConnectionManager cm = null;
    public String callAPIGet(String[] targets) throws URISyntaxException, IOException, ParseException {
        StringBuffer sb = new StringBuffer();
        for (String url : targets) {
            log.info("url : {}", url);
            String responseBody = null;
            CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();

            final org.apache.hc.client5.http.classic.methods.HttpGet httpget = new org.apache.hc.client5.http.classic.methods.HttpGet(url);
            httpget.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");
            httpget.setHeader("Accept", "application/json");

            log.debug("----------------------------------------");
            log.debug("Executing request {} {}" , httpget.getMethod() , httpget.getUri());

            final HttpClientContext clientContext = HttpClientContext.create();
            try (CloseableHttpResponse response = httpClient.execute(httpget, clientContext)) {

                responseBody = EntityUtils.toString(response.getEntity());
                sb.append(responseBody);
                log.debug("----------------------------------------");
                log.debug("{} ==> {}", response.getCode(), responseBody);

            }
        }
        return sb.toString();
    }
}
