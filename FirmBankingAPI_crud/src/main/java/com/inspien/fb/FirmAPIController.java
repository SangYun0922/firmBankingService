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

import javax.servlet.http.HttpServletRequest;


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

	@PostMapping("/{table}") //하나의 데이터를 테이블에 삽입할때
	public ResponseEntity dbInsertOne(@PathVariable String table ,@RequestBody(required = false) byte[] body) {
		log.info("table = {}", table);
		Gson gson = new Gson();
		switch (table) {
			case ("Customer") :
				CustMst custMst = gson.fromJson(new String(body), CustMst.class);
				JsonObject temp = new JsonObject();
				temp.addProperty("id", custMst.getCustId());
				if (custMstService.insertData(custMst) == 1) {
					return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
				}
				else {
					return new ResponseEntity<>("fail", HttpStatus.OK);
				}
			case ("Bank") :
				BankMst bankMst = gson.fromJson(new String(body), BankMst.class);
				JsonObject temp_2 = new JsonObject();
				temp_2.addProperty("id", bankMst.getBankId());
				if (bankMstService.insertData(bankMst) == 1) {
					return new ResponseEntity<>(gson.toJson(temp_2), HttpStatus.OK);
				}
				else {
					return new ResponseEntity<>("fail", HttpStatus.OK);
				}
			default:
				return new ResponseEntity<>("Can not found Table", HttpStatus.OK);
		}
	}

	@GetMapping("/read/{table}/{id}") //하나의 데이터를 읽을때
	public ResponseEntity dbReadOne(@PathVariable String table, @PathVariable String id) {
		log.info("table = {}", table);
		Gson gson = new Gson();
		JsonObject temp = new JsonObject();
		switch (table) {
			case ("Customer") :
				if (custMstService.readDataOne(id).size() == 1) {
					temp.addProperty("id", id);
					temp.addProperty("status", "success");
					return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
				}
				else {
					temp.addProperty("status", "fail");
					return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
				}
			case ("Bank") :
				if (bankMstService.readDataOne(id).size() == 1) {
					temp.addProperty("id", id);
					temp.addProperty("status", "success");
					return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
				}
				else {
					temp.addProperty("status", "fail");
					return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
				}
			default:
				temp.addProperty("status", "fail");
				return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
		}
	}

	@GetMapping("/{table}")
	public ResponseEntity getList(HttpServletRequest request, @PathVariable String table) {
		int limit = Integer.valueOf(request.getParameter("limit"));
		int page = Integer.valueOf(request.getParameter("page"));
		int start = limit * page - limit;
		String uri = request.getRequestURI();
		log.debug("uri = {}, {}, {} ", uri, limit, page);
		Gson gson = new Gson();
		HttpHeaders headers = new HttpHeaders();
		switch (table) {
			case ("Customer") :
				headers.add("X-Total-Count", String.valueOf(custMstService.totalCount()));
				List<CustMst> custMst = custMstService.readDataMany(start, limit);
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
				return new ResponseEntity<>(gson.toJson(custJson), headers, HttpStatus.OK);
			case ("Bank") :
				headers.add("X-Total-Count", String.valueOf(bankMstService.totalCount()));
				List<BankMst> bankMst = bankMstService.readDataMany(start, limit);
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
				return new ResponseEntity<>(gson.toJson(bankJson), headers, HttpStatus.OK);
			case ("Log") :
				headers.add("X-Total-Count", String.valueOf(txLogService.totalCount()));
				List<TxLog> txLog = txLogService.readDataMany(start, limit);
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
					temp.addProperty("NatvTrNo", e.getNatvTrNo());
					temp.addProperty("ErrCode", e.getErrCode());
					temp.addProperty("ErrMsg", e.getErrMsg());
					temp.addProperty("MsgId", e.getMsgId());
					temp.addProperty("EncData", e.getEncData());
					temp.addProperty("CustNm", e.getCustNm());
					temp.addProperty("BankNm", e.getBankNm());
					temp.addProperty("OrgCd", e.getOrgCd());
					txlogJson.add(temp);
				}
				return new ResponseEntity<>(gson.toJson(txlogJson), headers, HttpStatus.OK);
			case ("Stat") :
				headers.add("X-Total-Count", String.valueOf(txStatService.totalCount()));
				List<TxStat> txStat = txStatService.readDataMany(start, limit);
				JsonArray txstatJson = new JsonArray();
				int idx_Stat = 1;
				for (TxStat e : txStat) {
					JsonObject temp = new JsonObject();
					temp.addProperty("id", idx_Stat);
					temp.addProperty("CustId",e.getCustId());
					temp.addProperty("CustNm", e.getCustNm());
					temp.addProperty("TxDate", e.getTxDate());
					temp.addProperty("BankCd", e.getBankCd());
					temp.addProperty("BankNm", e.getBankNm());
					temp.addProperty("TxType", e.getTxType());
					temp.addProperty("TxCnt", e.getTxCnt());
					temp.addProperty("TxSize", e.getTxSize());
					temp.addProperty("OrgCd", e.getOrgCd());
					txstatJson.add(temp);
					idx_Stat++;
				}
				return new ResponseEntity<>(gson.toJson(txstatJson), headers, HttpStatus.OK);
			case ("Trace") :
				headers.add("X-Total-Count", String.valueOf(txTraceService.totalCount()));
				List<TxTrace> txTrace = txTraceService.readDataMany(start, limit);
				JsonArray txtraceJson = new JsonArray();
				int idx_Trace = 1;
				for (TxTrace e : txTrace) {
					JsonObject temp = new JsonObject();
					temp.addProperty("id", idx_Trace);
					temp.addProperty("CustId",e.getCustId());
					temp.addProperty("CustNm", e.getCustNm());
					temp.addProperty("TxDate", e.getTxDate());
					temp.addProperty("TxSequence",e.getTxSequence());
					temp.addProperty("TxStarted", e.getTxStarted());
					temp.addProperty("OrgCd", e.getOrgCd());
					txtraceJson.add(temp);
					idx_Trace++;
				}
				return new ResponseEntity<>(gson.toJson(txtraceJson), headers, HttpStatus.OK);
			default:
				return new ResponseEntity<>("Can not get Data", HttpStatus.OK);
		}
	}

	@PutMapping("/{table}/{id}") //데이터를 업데이트 할때,
	public ResponseEntity dbUpdate(@PathVariable String table, @PathVariable String id, @RequestBody(required = false) byte[] body) throws IOException, URISyntaxException, ParseException {
		log.info("table = {}", table);
		Gson gson = new Gson();
		JsonObject temp = new JsonObject();
		temp.addProperty("id", id);
		switch (table) {
			case ("Customer") :
				CustMst custMst = gson.fromJson(new String(body), CustMst.class);
				custMst.setCustId(id);
				if (custMstService.updateData(custMst) != 1) {
					temp.addProperty("Updating Failed", "No Target Services");
					return new ResponseEntity(gson.toJson(temp), HttpStatus.OK);
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
					log.debug("targets : {}", targets);
					if (targets.isEmpty()) {
						temp.addProperty("status", "No Target Services");
						return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
					} else {
						temp.addProperty("status", "Updating Success");
						getClient.callAPIGet(targets.toArray(new String[targets.size()]));
						return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
					}
				}
			case ("Bank") :
				BankMst bankMst = gson.fromJson(new String(body), BankMst.class);
				bankMst.setBankId(id);
				if (bankMstService.updateData(bankMst) != 1) {
					temp.addProperty("Updating Failed", "No Target Services");
					return new ResponseEntity(gson.toJson(temp), HttpStatus.OK);
				}
				else {
					List<String> targets = new ArrayList<>();
					String response = getClient.callAPIGet(new String[]{configMgmt.getEurekaServer()});
					JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
					JsonArray eurekaApps = jsonObject.get("applications").getAsJsonObject().get("application").getAsJsonArray();
					for (JsonElement e : eurekaApps) {
						JsonObject app = e.getAsJsonObject();
						if ((Objects.equals(app.get("name").getAsString(), "BANKSTATEMENT-SERVICE")) || (Objects.equals(app.get("name").getAsString(), "TRANSFER-SERVICE")) || (Objects.equals(app.get("name").getAsString(), "TRANSFERCHECK-SERVICE"))) {
							JsonArray instance = app.get("instance").getAsJsonArray();
							instance.iterator().forEachRemaining(i -> targets.add(i.getAsJsonObject().get("secureHealthCheckUrl").getAsString().replace("actuator/health", "") + "update"));
						}
					}
					log.debug("targets : {}", targets);
					if (targets.isEmpty()) {
						temp.addProperty("status", "No Target Services");
						return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
					} else {
						temp.addProperty("status", "Updating Success");
						getClient.callAPIGet(targets.toArray(new String[targets.size()]));
						return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
					}
				}
			default:
				return new ResponseEntity<>("Can not found Table", HttpStatus.OK);
		}
	}

	@DeleteMapping("/{table}/{id}") //하나의 데이터를 삭제할때
	public ResponseEntity dbDelete(@PathVariable String table, @PathVariable String id) {
		String ids = id.replace("filter=", "");
		JsonObject jsonObject = JsonParser.parseString(ids).getAsJsonObject();
		log.info("table = {} {} {}", table, id);
		JsonArray id_list = jsonObject.get("id").getAsJsonArray();

		Gson gson = new Gson();
		JsonObject temp = new JsonObject();
		JsonArray jsonArray = new JsonArray();

		if (id_list.size() == 0) {
			temp.addProperty("status", "Can not find id");
			return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
		}

		switch (table) {
			case ("Customer") :
				for (JsonElement e : id_list) {
					try {
						int res = custMstService.deleteData(e.getAsString());
						if (res == 1) {
							jsonArray.add(e.getAsString());
						}
					} catch (Exception err) {
						err.printStackTrace();
						continue;
					}
				}
				break;
			case ("Bank") :
				for (JsonElement e : id_list) {
					try {
						int res = bankMstService.deleteData(e.getAsString());
						if (res == 1) {
							jsonArray.add(e.getAsString());
						}
					} catch (Exception err) {
						err.printStackTrace();
						continue;
					}
				}
				break;
			default:
				temp.addProperty("status", "Can not find Table");
				return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
		}
		temp.add("data", jsonArray);
		return new ResponseEntity<>(gson.toJson(temp), HttpStatus.OK);
	}
}
