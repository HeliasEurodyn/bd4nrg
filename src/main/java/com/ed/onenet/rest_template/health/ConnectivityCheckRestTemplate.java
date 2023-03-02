package com.ed.onenet.rest_template.health;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConnectivityCheckRestTemplate {

    private final RestTemplate restTemplate;

    public ConnectivityCheckRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Object checkUrlConnectivity(String url) {

        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<Object> response =
                restTemplate.exchange(url ,
                        HttpMethod.GET,
                        httpEntity,
                        new ParameterizedTypeReference<Object>() {}
                );

        return response.getBody();
    }

}
