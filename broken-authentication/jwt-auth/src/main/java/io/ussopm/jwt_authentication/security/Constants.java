package io.ussopm.jwt_authentication.security;

import java.util.UUID;

public class Constants {
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 1*60*60; // 5 hours
    public static final String SECRET_KEY = UUID.randomUUID().toString();
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String AUTHORITIES_KEY = "scopes";
}
