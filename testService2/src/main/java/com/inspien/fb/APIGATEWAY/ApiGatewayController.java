package com.inspien.fb.APIGATEWAY;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGatewayController {

    @GetMapping("/welcome")
    public String welcome(){
        return "welcome to the second service";
    }
}
