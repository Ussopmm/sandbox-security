package io.ussopm.csrfbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.*;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class CsrfBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsrfBackendApplication.class, args);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();
        return http
                .formLogin(Customizer.withDefaults())
                .csrf(csrf -> csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler())
                        .csrfTokenRepository(csrfTokenRepository)
                        .sessionAuthenticationStrategy(new CsrfAuthenticationStrategy(csrfTokenRepository)))
//                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .build();
    }
}
