package com.inspien.fb.svc;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.inspien.fb.ApplicationContextProvider;
import com.inspien.fb.WriteLogs;
import com.inspien.fb.domain.CustMst;
import com.inspien.fb.mapper.CustMstMapper;
import com.inspien.fb.mapper.TxTraceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileTelegramManager {

	@Value("${van.duzn.telegramrepo}")
	static public String REPO = "config";

	private static String timezone = "Asia/Seoul";

	private static Map<String , Map<String, AtomicLong> > custCounter = new HashMap<String , Map<String, AtomicLong>>();

	private static boolean bInit = false;
	//	@Autowired
//	private TxTraceMapper txTraceMapper;
	private TxTraceMapper txTraceMapper = (TxTraceMapper) ApplicationContextProvider.getBean(TxTraceMapper.class);

	@Autowired
	private CustMstMapper custMstMapper;
	//	private WriteLogs writeLogs = (WriteLogs) ApplicationContextProvider.getBean(WriteLogs.class);
	@Autowired
	private WriteLogs writeLogs;

	public FileTelegramManager() {
		if(!bInit)
			init();
	}

	synchronized public void init() {
		String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(ZonedDateTime.now(ZoneId.of(timezone)));
		File dir = Paths.get(REPO, today).toFile();
		if(dir.exists()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				try {
					Path path = FileSystems.getDefault().getPath(dir.getPath(), files[i].getName());
					String s = Files.readString(path);
					String[] tokens = s.split(",");
					if(tokens != null && tokens.length == 3) {
						String orgCode = tokens[0].trim();
						String date = tokens[1].trim();
						long no = Long.valueOf(tokens[2].trim());

						Map<String, AtomicLong> m = new HashMap<String, AtomicLong>();
						m.put(today, new AtomicLong(no));
						custCounter.put(orgCode, m );
						log.debug("init.custCounter : {}", custCounter);
					}

				} catch (IOException e) {
					log.error("FileTelegramManager init failed.", e);
				}
			}
		}
		bInit = true;
	}

	public long getNextCounter(String orgCode) throws IOException {
		String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(ZonedDateTime.now(ZoneId.of(timezone)));
		List<CustMst> custMsts = custMstMapper.selectOne(orgCode);
		String custId = custMsts.get(0).getCustId();

		log.debug("getNextCounter.custCounter : {}", custCounter);
		long txNo = 0;
		if(custCounter.containsKey(orgCode)) { //custCounter에 해당 orgCode가 있으면
			if(custCounter.get(orgCode).containsKey(today)) { //오늘 거래 내역이 있으면
				log.debug("custCounter.get(orgCode).containsKey(today) : {}", custCounter.get(orgCode).containsKey(today));
				writeLogs.insertTxTraceLog(today,custId,1);
				txNo =  Long.parseLong(txTraceMapper.selectTxTrace(custId,today));

//				txNo = custCounter.get(orgCode).get(today).incrementAndGet();
			}
			else { //오늘 거래내역이 아닌경우
				// delete all data before put today
				custCounter.get(orgCode).clear();
				custCounter.get(orgCode).put(today, new AtomicLong(txNo));
			}
		}
		else {// config에 폴더가 존재하기 전 ==> 개시전문 전
			Map<String, AtomicLong> counter = new HashMap<String, AtomicLong>();
			writeLogs.insertTxTraceLog(today,custId,1);
			txNo =  Long.parseLong(txTraceMapper.selectTxTrace(custId,today));
			counter.put(today, new AtomicLong(txNo));
			custCounter.put(orgCode, counter);
		}
		syncCounter(orgCode, today, txNo);

		return txNo;
	}

	private void syncCounter(String orgCode, String date, long no) throws IOException {
		File dir = Paths.get(REPO, date).toFile();
		if(!dir.exists()) {
			dir.mkdirs();
		}

		File f = Paths.get(REPO, date, orgCode+".txt" ).toFile();

		StringBuffer sb = new StringBuffer();
		sb.append(orgCode).append(",").append(date).append(",").append(no);
		Files.write(Paths.get(REPO, date, orgCode+".txt" ), sb.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

	}
}