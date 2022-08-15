package com.inspien.fb.svc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Data
public class GetTargetService {
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

    public List<String> getTargets(String response) {
        List<String> targets = new ArrayList<>();
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        JsonArray eurekaApps = jsonObject.get("applications").getAsJsonObject().get("application").getAsJsonArray();
        for (JsonElement e : eurekaApps) {
            JsonObject app = e.getAsJsonObject();
            if ((Objects.equals(app.get("name").getAsString(), "BANKSTATEMENT-SERVICE")) || (Objects.equals(app.get("name").getAsString(), "TRANSFER-SERVICE")) || (Objects.equals(app.get("name").getAsString(), "TRANSFERCHECK-SERVICE"))) {
                JsonArray instance = app.get("instance").getAsJsonArray();
                instance.iterator().forEachRemaining(i -> targets.add(i.getAsJsonObject().get("secureHealthCheckUrl").getAsString().replace("actuator/health", "") + "update"));
            }
        }
        return targets;
    }
}
