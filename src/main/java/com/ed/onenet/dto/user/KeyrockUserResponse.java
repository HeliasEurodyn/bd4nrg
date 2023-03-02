package com.ed.onenet.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
@Builder
public class KeyrockUserResponse {

    private String app_id;
    private String id;
    private String displayName;
    private String email;
    private String username;
    private String password;
}
