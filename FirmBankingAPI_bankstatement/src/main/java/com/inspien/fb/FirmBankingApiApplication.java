package com.inspien.fb;

import com.inspien.fb.mapper.CustMstMapper;
import com.inspien.fb.svc.CustMstService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.List;

@SpringBootApplication
@EnableCaching
@Slf4j
@EnableDiscoveryClient
public class FirmBankingApiApplication implements CommandLineRunner {

	@Autowired
	CustMstService custMstService;

	@Autowired
	CustMstMapper custMstMapper;

	public static void main(String[] args) {
		SpringApplication.run(FirmBankingApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.debug("----------------------------------------");
		log.debug("start local cache initializing");
		log.debug("----------------------------------------");
		List<String> OrgCdList = custMstMapper.initData();
		for (String e : OrgCdList)
			custMstService.getData(e);
		log.debug("----------------------------------------");
		log.debug("complete local cache initializing");
		log.debug("----------------------------------------");
	}

}

