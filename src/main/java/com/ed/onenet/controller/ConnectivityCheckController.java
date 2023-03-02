package com.ed.onenet.controller;

import com.ed.onenet.service.health.ConnectivityCheckService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/check-connectivity")
@Slf4j
public class ConnectivityCheckController {

    private final ConnectivityCheckService connectivityCheckService;

    public ConnectivityCheckController(ConnectivityCheckService connectivityCheckService) {
        this.connectivityCheckService = connectivityCheckService;
    }

    @GetMapping
    public Map<String, String> check(@RequestHeader Map<String, String> headers,
                                     @RequestParam("data_app_url") String encodedDataAppUrl){
     return this.connectivityCheckService.check(headers, encodedDataAppUrl );
    }

}
