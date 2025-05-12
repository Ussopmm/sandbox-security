package io.ussopm.jwt_authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJson {

    private long id;
    private String username;
    private String fullName;
    private String password;
    private String email;
    private String role;
    private String created;
    private String token;

    public UserJson(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
}
