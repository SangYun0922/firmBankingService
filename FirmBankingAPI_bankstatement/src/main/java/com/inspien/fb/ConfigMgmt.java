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
@ConfigurationProperties(prefix = "master")
public class ConfigMgmt {  

    Map<String, Map<String, String >> customers = new HashMap<>();

}
