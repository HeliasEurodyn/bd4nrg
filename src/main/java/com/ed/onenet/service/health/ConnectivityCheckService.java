package com.ed.onenet.service.health;

import com.ed.onenet.rest_template.health.ConnectivityCheckRestTemplate;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Service
public class ConnectivityCheckService {

    private final ConnectivityCheckRestTemplate connectivityCheckRestTemplate;

    public ConnectivityCheckService(ConnectivityCheckRestTemplate connectivityCheckRestTemplate) {
        this.connectivityCheckRestTemplate = connectivityCheckRestTemplate;
    }

    public Map<String, String> check(Map<String, String> headers, String encodedDataAppUrl) {

        byte[] decodedBytes = Base64.getDecoder().decode(encodedDataAppUrl);
        String dataAppUrl = new String(decodedBytes);

        try {
            Object o = connectivityCheckRestTemplate.checkUrlConnectivity(dataAppUrl + "/about/version");
        } catch (Exception ex) {
            return Map.of("code", "4",
                    "message", "Data App is not accessible from the web");
        }


        return Map.of("code", "0",
                "message", "Connection is established!");
    }

}
