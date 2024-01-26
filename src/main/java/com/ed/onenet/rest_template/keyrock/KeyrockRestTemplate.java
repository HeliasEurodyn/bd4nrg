package com.ed.onenet.rest_template.keyrock;

import com.ed.onenet.dto.user.KeyrockAuthenticationResponse;
import com.ed.onenet.dto.user.KeyrockUserResponse;
import com.ed.onenet.dto.user.LoginDTO;
import com.ed.onenet.exception.login.LoginErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

@Service
public class KeyrockRestTemplate {

    private final RestTemplate restTemplate;

    @Value("${keyrock.uri}")
    private String keyrockUri;

    public KeyrockRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public KeyrockAuthenticationResponse auth2(LoginDTO loginDTO) {

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            httpHeaders.add("Authorization", "Basic ZTA0ZTA1MWQtMDAxZC00NzhlLWJlNGEtNjk2NjlhZjFjMzRkOjg4ZjcwYmEwLWRjMTAtNGE2Mi1hODZiLTYyODkyY2VjNmY4Mg==");

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("username", loginDTO.getUsername());
            map.add("password", loginDTO.getPassword());
            map.add("grant_type", "password");

            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

            ResponseEntity<KeyrockAuthenticationResponse> response =
                    restTemplate.exchange(
                            URI.create(keyrockUri + "/oauth2/token"),
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<KeyrockAuthenticationResponse>() {
                            }
                    );

            return response.getBody();

        } catch (HttpStatusCodeException ex) {
            return null;
        }
    }

    public KeyrockUserResponse getUserInfo(String token) {

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("Cookie", "session=eyJyZWRpciI6Ii8ifQ==; session.sig=TqcHvLKCvDVxuMk5xVfrKEP-GSQ");

            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(httpHeaders);

            ResponseEntity<KeyrockUserResponse> response =
                    restTemplate.exchange(
                            URI.create(keyrockUri + "/user?access_token=" + token),
                            HttpMethod.GET,
                            httpEntity,
                            new ParameterizedTypeReference<KeyrockUserResponse>() {
                            }
                    );

            return response.getBody();

        } catch (HttpStatusCodeException ex) {

            LoginErrorException loginErrorException = new LoginErrorException("User Invalid on Authorization Server");
            loginErrorException.setCode("001-5");
            loginErrorException.setVisible(true);
            loginErrorException.setCategory("LOGIN");

            throw loginErrorException;
        }
    }


    public String getAuthAdminToken(String name, String password) {

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("Cookie", "session=eyJyZWRpciI6Ii8ifQ==; session.sig=TqcHvLKCvDVxuMk5xVfrKEP-GSQ");

            Map<String, String> body = new HashMap<>();
            body.put("name",name);
            body.put("password",password);

            HttpEntity<Map> httpEntity = new HttpEntity<>(body, httpHeaders);

            ResponseEntity<Object> response =
                    restTemplate.exchange(
                            URI.create(keyrockUri + "/v3/auth/tokens"),
                            HttpMethod.POST,
                            httpEntity,
                            new ParameterizedTypeReference<Object>() {
                            }
                    );

            return response.getHeaders().get("X-Subject-Token").get(0);

        } catch (HttpStatusCodeException ex) {

            LoginErrorException loginErrorException = new LoginErrorException("User Credentials Are Invalid");
            loginErrorException.setCode("001-2");
            loginErrorException.setVisible(true);
            loginErrorException.setCategory("LOGIN");

            throw loginErrorException;
        }
    }

    public List<LinkedHashMap> getUsers(String token) {

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("X-Auth-Token", token);

            HttpEntity httpEntity = new HttpEntity(httpHeaders);

            ResponseEntity<LinkedHashMap> response =
                    restTemplate.exchange(
                            URI.create(keyrockUri + "/v1/users"),
                            HttpMethod.GET,
                            httpEntity,
                            new ParameterizedTypeReference<LinkedHashMap>() {
                            }
                    );

            return (List<LinkedHashMap>) response.getBody().get("users");

        } catch (HttpStatusCodeException ex) {

            LoginErrorException loginErrorException = new LoginErrorException("Error");
            loginErrorException.setCode("001-2");
            loginErrorException.setVisible(true);
            loginErrorException.setCategory("LOGIN");

            throw loginErrorException;
        }
    }


    public List<LinkedHashMap> getOrganizations(String token) {

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("X-Auth-Token", token);

            HttpEntity httpEntity = new HttpEntity(httpHeaders);

            ResponseEntity<LinkedHashMap> response =
                    restTemplate.exchange(
                            URI.create(keyrockUri + "/v1/organizations"),
                            HttpMethod.GET,
                            httpEntity,
                            new ParameterizedTypeReference<LinkedHashMap>() {
                            }
                    );

            return (List<LinkedHashMap>) response.getBody().get("organizations");

        } catch (HttpStatusCodeException ex) {

            LoginErrorException loginErrorException = new LoginErrorException("Error");
            loginErrorException.setCode("001-2");
            loginErrorException.setVisible(true);
            loginErrorException.setCategory("LOGIN");

            throw loginErrorException;
        }
    }

    public List<LinkedHashMap> getApplications(String token) {

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("X-Auth-Token", token);

            HttpEntity httpEntity = new HttpEntity(httpHeaders);

            ResponseEntity<LinkedHashMap> response =
                    restTemplate.exchange(
                            URI.create(keyrockUri + "/v1/applications"),
                            HttpMethod.GET,
                            httpEntity,
                            new ParameterizedTypeReference<LinkedHashMap>() {
                            }
                    );

            return (List<LinkedHashMap>) response.getBody().get("applications");

        } catch (HttpStatusCodeException ex) {

            LoginErrorException loginErrorException = new LoginErrorException("Error");
            loginErrorException.setCode("001-2");
            loginErrorException.setVisible(true);
            loginErrorException.setCategory("LOGIN");

            throw loginErrorException;
        }
    }

    public List<LinkedHashMap> getUsersOfOrganization(String organizationId, String token) {

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add("X-Auth-Token", token);

            HttpEntity httpEntity = new HttpEntity(httpHeaders);

            ResponseEntity<LinkedHashMap> response =
                    restTemplate.exchange(
                            URI.create(keyrockUri + "/v1/organizations/"+organizationId+"/users"),
                            HttpMethod.GET,
                            httpEntity,
                            new ParameterizedTypeReference<LinkedHashMap>() {
                            }
                    );

            return (List<LinkedHashMap>) response.getBody().get("organization_users");

        } catch (HttpStatusCodeException ex) {

            LoginErrorException loginErrorException = new LoginErrorException("Error");
            loginErrorException.setCode("001-2");
            loginErrorException.setVisible(true);
            loginErrorException.setCategory("LOGIN");

            throw loginErrorException;
        }
    }


}
