package com.ed.onenet.dto.user;

import com.ed.onenet.config.AppConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;


@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class UserDTO {

    private String id;

    private String username;

    private String email;

    private String password;

    private String repeatPassword;

    private AppConstants.Types.UserStatus status;

    private String dateformat;

    private Object sidebarMenu;

    private Object headerMenu;

    private String loginNavCommand;

    private String searchNavCommand;

    private String provider;

    private Object roles;

    private Object defaultLanguage;

    private Object languages;

    private Object currentLanguage;

}
