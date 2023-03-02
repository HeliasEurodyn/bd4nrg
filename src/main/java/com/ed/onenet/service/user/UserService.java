package com.ed.onenet.service.user;

import com.ed.onenet.dto.user.KeyrockAuthenticationResponse;
import com.ed.onenet.dto.user.KeyrockUserResponse;
import com.ed.onenet.dto.user.LoginDTO;
import com.ed.onenet.repository.user.UserRepository;
import com.ed.onenet.rest_template.keyrock.KeyrockRestTemplate;
import com.ed.onenet.rest_template.user.UserRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    private final UserRestTemplate userRestTemplate;

    private final KeyrockRestTemplate keyrockRestTemplate;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRestTemplate userRestTemplate, KeyrockRestTemplate keyrockRestTemplate, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRestTemplate = userRestTemplate;
        this.keyrockRestTemplate = keyrockRestTemplate;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity authenticate(LoginDTO loginDTO) {
        KeyrockAuthenticationResponse keyrockAuthenticationResponse = keyrockRestTemplate.auth2(loginDTO);

        if (keyrockAuthenticationResponse != null) {
            KeyrockUserResponse keyrockUserResponse = keyrockRestTemplate.getUserInfo(keyrockAuthenticationResponse.getAccess_token());
            keyrockUserResponse.setPassword(loginDTO.getPassword());

            this.userRepository.registerNewUser(keyrockUserResponse);

            String password = passwordEncoder.encode(keyrockUserResponse.getPassword());
            this.userRepository.resetPassword(keyrockUserResponse.getId(), password);
        }

        return this.userRestTemplate.authenticate(loginDTO);
    }

    public void refreshKeyrockUsers() {

        String adminToken = keyrockRestTemplate.getAuthAdminToken("admin@test.com", "1234");
        List<LinkedHashMap> keyrockUserResponseMaps = keyrockRestTemplate.getUsers(adminToken);

        keyrockUserResponseMaps.forEach(keyrockUserResponseMap -> {

            KeyrockUserResponse keyrockUserResponse = new KeyrockUserResponse().setId((String) keyrockUserResponseMap.get("id")).setUsername((String) keyrockUserResponseMap.get("username")).setEmail((String) keyrockUserResponseMap.get("email")).setDisplayName((String) keyrockUserResponseMap.get("description")).setPassword(UUID.randomUUID().toString());

            this.userRepository.registerNewUser(keyrockUserResponse);
        });

    }

    @Transactional
    public void refreshKeyrockOrganizations() {

        String adminToken = keyrockRestTemplate.getAuthAdminToken("admin@test.com", "1234");
        List<LinkedHashMap> keyrockOrgResponseMaps = keyrockRestTemplate.getOrganizations(adminToken);

        keyrockOrgResponseMaps.forEach(keyrockOrgResponseMap -> {

            Map<String, String> organization = (Map<String, String>) keyrockOrgResponseMap.get("Organization");
            this.userRepository.registerOrganization(organization.get("id"), organization.get("name"), organization.get("description"));

            String organizationId = organization.get("id");
            this.refreshKeyrockOrganizationUsers(organizationId, adminToken);
        });

    }

    public void refreshKeyrockOrganizationUsers(String organizationId, String adminToken) {
        List<LinkedHashMap> keyrockOrgUsersResponseMaps = keyrockRestTemplate.getUsersOfOrganization(organizationId, adminToken);

        keyrockOrgUsersResponseMaps.forEach(map -> {
            this.userRepository.registerOrganizationUserJoin((String) map.get("user_id"), (String) map.get("organization_id"));
        });
    }

}
