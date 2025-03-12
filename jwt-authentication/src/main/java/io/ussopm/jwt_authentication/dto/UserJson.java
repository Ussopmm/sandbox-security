package io.ussopm.jwt_authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserJson {

    private long id;
    private String username;
    private String password;
    private String role;
    private String created;

    public UserJson(long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
