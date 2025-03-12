package io.ussopm.jwt_authentication.dto;

import lombok.Data;

@Data
public class AuthorizedUser {

    private String token;
    private String username;
    private String role;
    private boolean isExpired;

    public AuthorizedUser(String token) {
        this.token = token;
    }

    public AuthorizedUser(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }
}
