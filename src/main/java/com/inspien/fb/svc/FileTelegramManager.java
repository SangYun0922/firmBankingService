package com.inspien.fb.svc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
					}
					
				} catch (IOException e) {
					log.error("FileTelegramManager init failed.", e);
				}
			}
		}
		bInit = true;
	}

	public long getNowCounter(String orgCode) {
		String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(ZonedDateTime.now(ZoneId.of(timezone)));
		long txNo = 0;
		if (custCounter.containsKey(orgCode)) {
			if (custCounter.get(orgCode).containsKey(today)) {
				txNo = custCounter.get(orgCode).get(today).get();
			}
		}
		return txNo;
	}
	
	public long getNextCounter(String orgCode) throws IOException {
		String today = DateTimeFormatter.ofPattern("yyyyMMdd").format(ZonedDateTime.now(ZoneId.of(timezone)));
		
		long txNo = 1;
		if(custCounter.containsKey(orgCode)) {
			if(custCounter.get(orgCode).containsKey(today)) {
				txNo = custCounter.get(orgCode).get(today).incrementAndGet();
			}
			else {
				// delete all data before put today
				custCounter.get(orgCode).clear();
				custCounter.get(orgCode).put(today, new AtomicLong(txNo));
			}
		}
		else {
			Map<String, AtomicLong> counter = new HashMap<String, AtomicLong>();
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
