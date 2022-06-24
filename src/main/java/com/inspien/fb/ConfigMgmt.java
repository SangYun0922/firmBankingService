package com.inspien.fb;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.inspien.fb.model.Customer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "master") // yaml파일에 명시되어있는 프로퍼티를 가져와서 자바클래스에 값을 바인딩하여 사용할 수 있게한다.
/*
master:
        customers:
            10000262:
                name: SAP테스트
                callback: https://p200029-iflmap.hcisbp.ap1.hana.ondemand.com/http/BankStatementPost
            10000263:
                name: 인스피언(주)
*/
public class ConfigMgmt {  

    Map<String, Map<String, String >> customers = new HashMap<>();

}
