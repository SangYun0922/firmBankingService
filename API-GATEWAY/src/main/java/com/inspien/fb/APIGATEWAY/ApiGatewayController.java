package com.inspien.fb.APIGATEWAY;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGatewayController {

    @GetMapping("/ping")
    public String welcome(){
        return "welcome to the gateway service";
    }
}
