package com.inspien.fb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.*;

import com.google.gson.*;
import com.inspien.fb.domain.BankMst;
import com.inspien.fb.domain.CustMst;

import com.inspien.fb.svc.BankMstService;
import com.inspien.fb.svc.CustMstService;
import com.inspien.fb.svc.GetClient;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
public class FirmAPIController {
	//2022.07.07 created;
	@Autowired
	CustMstService custMstService;

	@Autowired
	BankMstService bankMstService;

	@Autowired
	GetClient getClient;

	@Autowired
	ConfigMgmt configMgmt;

	@GetMapping("/ping")
	public APIInfo ping() {
		APIInfo info = APIInfo.builder().app("FirmBankingAPI").ver("1.0").timestamp(LocalDateTime.now()).build();
		return info;
	}

	@PostMapping("/insert/{table}") //하나의 데이터를 테이블에 삽입할때
	public String dbInsertOne(@PathVariable String table ,@RequestBody(required = false) byte[] body) {
		log.info("table = {}", table);
		Gson gson = new Gson();
		switch (table) {
			case ("CustMst") :
				CustMst custMst = gson.fromJson(new String(body), CustMst.class);
				if (custMstService.insertData(custMst) == 1)
					return "Insert success";
				else
					return "Insert failed";
			case ("BankMst") :
				BankMst bankMst = gson.fromJson(new String(body), BankMst.class);
				log.info("BankMst : {}", bankMst);
				if (bankMstService.insertData(bankMst) == 1)
					return "Insert success";
				else
					return "Insert failed";
			default:
				return "Can not found Table";
		}
	}

	@GetMapping("/read/{table}/{id}") //하나의 데이터를 읽을때
	public String dbReadOne(@PathVariable String table, @PathVariable String id) {
		log.info("table = {}", table);
		Gson gson = new Gson();
		switch (table) {
			case ("CustMst") :
				return gson.toJson(custMstService.readDataOne(id));
			case ("BankMst") :
				return gson.toJson(bankMstService.readDataOne(id));
			default:
				return "Can not found Table";
		}
	}

	@GetMapping("/readlist/{table}") //테이블 전체 데이터를 읽을때
	public String dbReadMany(@PathVariable String table) {
		log.info("table = {}", table);
		Gson gson = new Gson();
		switch (table) {
			case ("CustMst") :
				return gson.toJson(custMstService.readDataMany());
			case ("BankMst") :
				return gson.toJson(bankMstService.readDataMany());
			default:
				return "Can not found Table";
		}
	}

	@PutMapping("/update/{table}/{id}") //데이터를 업데이트 할때,
	public String dbUpdate(@PathVariable String table, @PathVariable String id, @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException, ParseException {
		log.info("table = {}", table);
		Gson gson = new Gson();
		switch (table) {
			case ("CustMst") :
				CustMst custMst = gson.fromJson(new String(body), CustMst.class);
				custMst.setCustId(id);
				log.info("custMst : {}", custMst);
				if (custMstService.updateData(custMst) != 1) {
					return "updating failed";
				}
				else {
					List<String> targets = new ArrayList<>();
					String response = getClient.callAPIGet(new String[]{configMgmt.getEurekaServer()});
					JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
					JsonArray eurekaApps = jsonObject.get("applications").getAsJsonObject().get("application").getAsJsonArray();

					for (JsonElement e : eurekaApps) {
						JsonObject app = e.getAsJsonObject();
						if ((Objects.equals(app.get("name").getAsString(), "BANKSTATEMENT-SERVICE")) || (Objects.equals(app.get("name").getAsString(), "TRANSFER-SERVICE"))) {
							JsonArray instance = app.get("instance").getAsJsonArray();
							instance.iterator().forEachRemaining(i -> targets.add(i.getAsJsonObject().get("secureHealthCheckUrl").getAsString().replace("actuator/health", "") + "update"));
						}
					}

					log.info("targets : {}", targets);
					getClient.callAPIGet(targets.toArray(new String[targets.size()]));
					return "updating success";
				}
			case ("BankMst") :
				BankMst bankMst = gson.fromJson(new String(body), BankMst.class);
				bankMst.setBankId(id);
				log.info("bankMst : {}", bankMst);
				if (bankMstService.updateData(bankMst) != 1) {
					return "updating failed";
				}
				break;
			default:
				return "Can not found Table";
		}
			return "updating success";
	}

	@GetMapping("delete/{table}/{id}") //하나의 데이터를 삭제할때
	public String dbDelete(@PathVariable String table, @PathVariable String id) {
		log.info("table = {}", table);
		switch (table) {
			case ("CustMst") :
				if (custMstService.deleteData(id) == 1)
					return "Delete success";
				else
					return "Delete failed";
			case ("BankMst") :
				if (bankMstService.deleteData(id) == 1)
					return "Delete success";
				else
					return "Delete failed";
			default:
				return "Can not found Table";
		}
	}
}
