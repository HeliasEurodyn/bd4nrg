package com.ed.onenet.controller.user;

import com.ed.onenet.dto.user.LoginDTO;
import com.ed.onenet.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticate(@RequestBody LoginDTO loginDTO) {
        ResponseEntity response = userService.authenticate(loginDTO);
        userService.refreshKeyrockUsers();
        userService.refreshKeyrockOrganizations();
        return response;
    }

}
