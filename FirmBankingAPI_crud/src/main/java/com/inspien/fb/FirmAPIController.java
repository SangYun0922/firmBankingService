package com.inspien.fb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.google.gson.*;
import com.inspien.fb.domain.*;

import com.inspien.fb.svc.*;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	TxLogService txLogService;
	@Autowired
	TxStatService txStatService;
	@Autowired
	TxTraceService txTraceService;

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

	@GetMapping("/{table}")
	public ResponseEntity getList(@PathVariable String table) {
		Gson gson = new Gson();
		HttpHeaders headers = new HttpHeaders();
		switch (table) {
			case ("Customer") :
				List<CustMst> custMst = custMstService.readDataMany();
				int cust_length = custMst.size();
				headers.add("X-Total-Count", String.valueOf(cust_length));
				JsonArray custJson = new JsonArray();
				for (CustMst e : custMst) {
					JsonObject temp = new JsonObject();
					temp.addProperty("id", e.getCustId());
					temp.addProperty("CustNm", e.getCustNm());
					temp.addProperty("OrgCd", e.getOrgCd());
					temp.addProperty("CallbackURL", e.getCallbackURL());
					temp.addProperty("ApiKey", e.getApiKey());
					temp.addProperty("PriContactNm", e.getPriContactNm());
					temp.addProperty("PriContactTel", e.getPriContactTel());
					temp.addProperty("PriContactEmail", e.getPriContactEmail());
					temp.addProperty("SecContactNm", e.getSecContactNm());
					temp.addProperty("SecContactTel", e.getSecContactTel());
					temp.addProperty("SecContactEmail", e.getSecContactEmail());
					temp.addProperty("TxSequence", e.getTxSequence());
					temp.addProperty("InUse", e.getInUse());
					temp.addProperty("CreatedAt", String.valueOf(e.getCreatedAt()));
					temp.addProperty("UpdatedAt", String.valueOf(e.getUpdatedAt()));
					custJson.add(temp);
				}
				System.out.println("custJson.getAsString() = " + gson.toJson(custJson));
				return new ResponseEntity<>(gson.toJson(custJson), headers, HttpStatus.OK);
			case ("Bank") :
				List<BankMst> bankMst = bankMstService.readDataMany();
				int bank_length = bankMst.size();
				headers.add("X-Total-Count", String.valueOf(bank_length));
				JsonArray bankJson = new JsonArray();
				for (BankMst e : bankMst) {
					JsonObject temp = new JsonObject();
					temp.addProperty("id", e.getBankId());
					temp.addProperty("BankCd", e.getBankCd());
					temp.addProperty("BankNm", e.getBankNm());
					temp.addProperty("SwiftCd", e.getSwiftCd());
					temp.addProperty("CreatedAt", String.valueOf(e.getCreatedAt()));
					temp.addProperty("UpdatedAt", String.valueOf(e.getUpdatedAt()));
					bankJson.add(temp);
				}
				System.out.println("bankJson.getAsString() = " + gson.toJson(bankJson));
				return new ResponseEntity<>(gson.toJson(bankJson), headers, HttpStatus.OK);
			case ("Log") :
				List<TxLog> txLog = txLogService.readDataMany();
				int txlog_length = txLog.size();
				headers.add("X-Total-Count", String.valueOf(txlog_length));
				JsonArray txlogJson = new JsonArray();
				for (TxLog e : txLog) {
					JsonObject temp = new JsonObject();
					temp.addProperty("id",e.getTxIdx());
					temp.addProperty("CustId",e.getCustId());
					temp.addProperty("TxDate", e.getTxDate());
					temp.addProperty("TelegramNo", e.getTelegramNo());
					temp.addProperty("TxType", e.getTxType());
					temp.addProperty("BankCd", e.getBankCd());
					temp.addProperty("Size",e.getSize());
					temp.addProperty("RoundTrip", e.getRoundTrip());
					temp.addProperty("StmtCnt", e.getStmtCnt());
					temp.addProperty("Status", e.getStatus());
					temp.addProperty("StartDT", String.valueOf(e.getStartDT()));
					temp.addProperty("EndDT", String.valueOf(e.getEndDT()));
					temp.addProperty("EncData", e.getEncData());
					temp.addProperty("NatvTrNo", e.getNatvTrNo());
					temp.addProperty("ErrCode", e.getErrCode());
					temp.addProperty("ErrMsg", e.getErrMsg());
					txlogJson.add(temp);
				}
				System.out.println("gson.toJson(txlogJson) = " + gson.toJson(txlogJson));
				return new ResponseEntity<>(gson.toJson(txlogJson), headers, HttpStatus.OK);
			case ("Stat") :
				List<TxStat> txStat = txStatService.readDataMany();
				int txstat_length = txStat.size();
				headers.add("X-Total-Count", String.valueOf(txstat_length));
				JsonArray txstatJson = new JsonArray();
				int idx_Stat = 1;
				for (TxStat e : txStat) {
					JsonObject temp = new JsonObject();
					temp.addProperty("id", idx_Stat);
					temp.addProperty("CustId",e.getCustId());
					temp.addProperty("TxDate", e.getTxDate());
					temp.addProperty("BankCd", e.getBankCd());
					temp.addProperty("TxType", e.getTxType());
					temp.addProperty("TxCnt", e.getTxCnt());
					temp.addProperty("TxSize", e.getTxSize());
					txstatJson.add(temp);
					idx_Stat++;
				}
				log.debug("gson.toJson(txstatJson) = " + gson.toJson(txstatJson));
				return new ResponseEntity<>(gson.toJson(txstatJson), headers, HttpStatus.OK);
			case ("Trace") :
				List<TxTrace> txTrace = txTraceService.readDataMany();
				int txtrace_length = txTrace.size();
				headers.add("X-Total-Count", String.valueOf(txtrace_length));
				JsonArray txtraceJson = new JsonArray();
				int idx_Trace = 1;
				for (TxTrace e : txTrace) {
					JsonObject temp = new JsonObject();
					temp.addProperty("id", idx_Trace);
					temp.addProperty("CustId",e.getCustId());
					temp.addProperty("TxDate", e.getTxDate());
					temp.addProperty("TxSequence",e.getTxSequence());
					temp.addProperty("TxStarted", e.getTxStarted());
					txtraceJson.add(temp);
					idx_Trace++;
				}
				log.debug("gson.toJson(txtraceJson) = " + gson.toJson(txtraceJson));
				return new ResponseEntity<>(gson.toJson(txtraceJson), headers, HttpStatus.OK);
			default:
				return new ResponseEntity<>("Can not get Data", HttpStatus.OK);
		}
	}
//	@GetMapping("/readlist") //테이블 전체 데이터를 읽을때
//	public String dbReadMany() {
//		List<CustMst> custMst = custMstService.readDataMany();
//		List<BankMst> bankMst = bankMstService.readDataMany();
//		Gson gson = new Gson();
//		JsonObject jsonObject = new JsonObject();
//		try {
//			JsonArray custJson = new JsonArray();
//			JsonArray bankJson = new JsonArray();
//			for (CustMst e : custMst) {
//				JsonObject temp = new JsonObject();
//				temp.addProperty("CustId", e.getCustId());
//				temp.addProperty("CustNm", e.getCustNm());
//				temp.addProperty("OrgCd", e.getOrgCd());
//				temp.addProperty("CallbankURL", e.getCallbackURL());
//				temp.addProperty("ApiKey", e.getApiKey());
//				temp.addProperty("PriContactNm", e.getPriContactNm());
//				temp.addProperty("PriContactTel", e.getPriContactTel());
//				temp.addProperty("PriContactEmail", e.getPriContactEmail());
//				temp.addProperty("SecContactNm", e.getSecContactNm());
//				temp.addProperty("SecContactTel", e.getSecContactTel());
//				temp.addProperty("SecContactEmail", e.getSecContactEmail());
//				temp.addProperty("TxSequence", e.getTxSequence());
//				temp.addProperty("", e.getCustId());
//				custJson.add(temp);
//			}
//			for (BankMst e : bankMst) {
//				JsonObject temp = new JsonObject();
//				temp.addProperty("BankId", e.getBankId());
//				temp.addProperty("BankCd", e.getBankCd());
//				temp.addProperty("BankNm", e.getBankNm());
//				temp.addProperty("SwiftCd", e.getSwiftCd());
//				bankJson.add(temp);
//			}
//			jsonObject.add("Customers", custJson);
//			jsonObject.add("Banks", bankJson);
//		} catch (JsonIOException e) {
//			e.printStackTrace();
//		}
//		return gson.toJson(jsonObject);
//	}

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
