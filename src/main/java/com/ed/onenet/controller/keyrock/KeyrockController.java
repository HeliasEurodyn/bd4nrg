package com.ed.onenet.controller.keyrock;

import com.ed.onenet.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/keyrock")
@Slf4j
public class KeyrockController {

    private final UserService userService;

    public KeyrockController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/sync")
    public Map<String, String> sync() {
        userService.refreshKeyrockUsers();
        return Collections.singletonMap("status","ok");
    }

    @PostMapping(value = "/sync/org")
    public Map<String, String> syncOrg() {
        userService.refreshKeyrockOrganizations();
        return Collections.singletonMap("status","ok");
    }

    @PostMapping(value = "/sync/app")
    public Map<String, String> syncApp() {
        userService.refreshKeyrockApplications();
        return Collections.singletonMap("status","ok");
    }

}
