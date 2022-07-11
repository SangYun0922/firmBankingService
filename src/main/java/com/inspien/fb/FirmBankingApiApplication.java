package com.inspien.fb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FirmBankingApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(FirmBankingApiApplication.class, args);
	}

}
