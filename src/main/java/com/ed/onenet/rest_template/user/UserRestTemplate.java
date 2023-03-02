package com.ed.onenet.rest_template.user;

import com.ed.onenet.dto.user.JwtAuthenticationResponse;
import com.ed.onenet.dto.user.LoginDTO;
import com.ed.onenet.exception.login.LoginErrorException;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserRestTemplate {

    private final RestTemplate restTemplate;

    @Value("${sofia.uri}")
    private String sofiaUri;

    public UserRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> authenticate(LoginDTO loginDTO) {
        try {

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "application/json");
            HttpEntity<LoginDTO> httpEntity = new HttpEntity<>(loginDTO, httpHeaders);

            ResponseEntity<JwtAuthenticationResponse> response = restTemplate.exchange(this.sofiaUri + "/user/auth",
                    HttpMethod.POST,
                    httpEntity,
                    new ParameterizedTypeReference<JwtAuthenticationResponse>() {
                    }
            );

            return response;

        } catch (HttpStatusCodeException ex) {

            LoginErrorException loginErrorException = new LoginErrorException("User Credentials Are Invalid");
            loginErrorException.setCode("001-2");
            loginErrorException.setVisible(true);
            loginErrorException.setCategory("LOGIN");
            throw loginErrorException;

//            String errorMessage = ex.getMessage().substring(7, ex.getMessage().length() - 1);
//            DocumentContext documentContext = JsonPath.parse(errorMessage);
//            String code = documentContext.read("$.code");
//            String message = documentContext.read("$.message");
//            String category = documentContext.read("$.category");
//
//            LoginErrorException loginErrorException = new LoginErrorException(message);
//            loginErrorException.setCode(code);
//            loginErrorException.setVisible(true);
//            loginErrorException.setCategory(category);
//
//            throw loginErrorException;
        }
    }
}
