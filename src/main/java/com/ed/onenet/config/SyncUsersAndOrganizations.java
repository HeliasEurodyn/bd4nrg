package com.ed.onenet.config;

import com.ed.onenet.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Configuration
@EnableScheduling
@Slf4j
public class SyncUsersAndOrganizations {

    private final UserService userService;

    public SyncUsersAndOrganizations(UserService userService) {
        this.userService = userService;
    }


    @Scheduled(cron = "0 */1 * ? * *", zone = "Europe/Athens")
    public void sync() {
        userService.refreshKeyrockUsers();
        userService.refreshKeyrockOrganizations();
    }

}
