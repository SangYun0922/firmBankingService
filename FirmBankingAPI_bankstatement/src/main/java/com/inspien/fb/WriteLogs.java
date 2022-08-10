package com.inspien.fb;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.inspien.fb.domain.TxLog;
import com.inspien.fb.domain.TxStat;
import com.inspien.fb.domain.TxTrace;
import com.inspien.fb.mapper.TxLogMapper;
import com.inspien.fb.mapper.TxStatMapper;
import com.inspien.fb.mapper.TxTraceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Component
public class WriteLogs {

    @Value("${spring.mybatis.aes-encrypt-key}")
    private String key;

    @Autowired
    private TxLogMapper txLogMapper;

    @Autowired
    private TxTraceMapper txTraceMapper;
    @Autowired
    private TxStatMapper txStatMapper;


    DecimalFormat intFormatter = new DecimalFormat("000");
    private String transactionIdx;
    private String customerId;

    Gson gson = new Gson();


    public void insertDataBaseLog(String custId, LocalDateTime startDateTime, LocalDateTime endDateTime, int TxType,
                                  long Size, double RoundTrip, String request, String response, String idx){
        log.info("start db insert");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        JsonObject txLogByJson = new JsonObject();
        TxLog txLog = null;
        JsonObject reqJson = JsonParser.parseString(request).getAsJsonObject();
        JsonObject resJson = JsonParser.parseString(response).getAsJsonObject();
//      System.out.println("inner method respond ==> "+resJson);
//      System.out.println("inner method request ==> "+reqJson);
        String txSequence = TxType == 3?"null":txTraceMapper.selectTxTrace(custId,dateFormat.format(startDateTime));
        if (Objects.equals(resJson.get("status").getAsInt(), 200)){
            if(TxType==1 || TxType==2) {
                txLogByJson.addProperty("NatvTrNo",resJson.get("natv_tr_no").getAsString());
            } else if(Objects.equals(TxType,3)){
                txLogByJson.addProperty("NatvTrNo",reqJson.get("natv_tr_no").getAsString());
            }
        }else{
            if(Objects.equals(TxType,3)){
                txLogByJson.addProperty("NatvTrNo",reqJson.get("natv_tr_no").getAsString());
            }
            txLogByJson.addProperty("ErrCode",resJson.get("error_code").getAsString());
            txLogByJson.addProperty("ErrMsg",resJson.get("error_message").getAsString());
        }
        txLogByJson.addProperty("TxIdx",idx);
        txLogByJson.addProperty("CustId",custId);
        txLogByJson.addProperty("TxDate", dateFormat.format(startDateTime));
        txLogByJson.addProperty("TelegramNo",TxType == 1?txSequence:null);
        txLogByJson.addProperty("MsgId",TxType == 3?null:reqJson.get("msg_id").getAsString());
        txLogByJson.addProperty("TxType", TxType); //transfer = 1; read = 2; bankstatment = 3
        txLogByJson.addProperty("BankCd",reqJson.get(TxType == 1?"rv_bank_code":(TxType==2?"drw_bank_code":"bank_code")).getAsString());
        txLogByJson.addProperty("Size",Size);
        txLogByJson.addProperty("RoundTrip",RoundTrip);
        txLogByJson.addProperty("StmtCnt", 1);
        txLogByJson.addProperty("Status",resJson.get("status").getAsString());
        txLogByJson.addProperty("StartDT",String.valueOf((startDateTime)) + ZoneId.of("+09:00"));
        txLogByJson.addProperty("EncData", request);
        txLogByJson.addProperty("EndDT", String.valueOf((endDateTime)) + ZoneId.of("+09:00"));

        txLog = gson.fromJson(txLogByJson,TxLog.class);
        log.debug("txlog : {}", txLog);
        txLogMapper.logAdd(key,txLog);
    }

    public void insertFileLog(int cnt,int txType,String txIdx,String custId,LocalDateTime dateTime,String to, String from,String data) throws IOException {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try{
            if (cnt == 1){
                this.transactionIdx = txIdx+txType;
                this.customerId = custId;
                fis = new FileInputStream("logs/format.txt");
                fos = new FileOutputStream(String.format("../logs/%s.txt",transactionIdx));
                int readData = 0;
                while(readData !=-1){
                    readData = fis.read();
                    fos.write(readData);
                }
                fos.write("\n".getBytes());

            }

            String fromFormat = dateTimeFormatter.format(dateTime);
            fos = new FileOutputStream(String.format("../logs/%s.txt",transactionIdx),true);
            fos.write(String.format("%d\t",txType).getBytes());
            fos.write(String.format("%s\t",to).getBytes());
            fos.write(String.format("%s\t",from).getBytes());
            fos.write(String.format("%s\t",transactionIdx).getBytes());
            fos.write(String.format("%s\t",customerId).getBytes());
            fos.write(String.format("%s\t",dateFormat.format(dateTime)).getBytes());
            fos.write(Objects.equals(to,"server")?String.format("%s\t-------------------\t",fromFormat).getBytes():String.format("-------------------\t%s\t",fromFormat).getBytes());
            fos.write(data.getBytes());
            fos.write("\n".getBytes());

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    //transfer일때만 사용
    public void insertTxTraceLog(String dateTime,String custId,long telegramNo) throws IOException {
        JsonObject txTraceByJson = new JsonObject();
        TxTrace txTrace = null;

        txTraceByJson.addProperty("CustId",custId);
        txTraceByJson.addProperty("TxDate",dateTime);
        txTraceByJson.addProperty("TxSequence",telegramNo);
        txTraceByJson.addProperty("TxStarted",telegramNo==0?"N":"Y");
        txTrace = gson.fromJson(txTraceByJson,TxTrace.class);

        txTraceMapper.upsertTxTrace(txTrace);
    }

    public void insertTxStatLog(String dateTime, String custId,int txType,long size,String bankCd ){
        JsonObject txStatByJson = new JsonObject();
        TxStat txStat = null;

        txStatByJson.addProperty("CustId",custId);
        txStatByJson.addProperty("TxDate",dateTime);
        txStatByJson.addProperty("BankCd",bankCd);
        txStatByJson.addProperty("TxType",txType);
        txStatByJson.addProperty("TxCnt",1);
        txStatByJson.addProperty("TxSize",size);

        txStat = gson.fromJson(txStatByJson,TxStat.class);
        txStatMapper.insertTxStat(txStat);
    }


}