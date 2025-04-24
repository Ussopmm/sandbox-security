package io.ussopm.csrfbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@SpringBootApplication
public class CsrfBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsrfBackendApplication.class, args);
    }


//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .formLogin(Customizer.withDefaults())
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(cors -> cors.configurationSource(request -> {
//                    var config = new CorsConfiguration();
//                    config.setAllowCredentials(true);
//                    config.setAllowedMethods(List.of("*"));
//                    config.setAllowedHeaders(List.of("*"));
//                    config.setAllowedOriginPatterns(List.of("*"));
//
//                    return config;
//                }))
//                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
//                .build();
//    }
}
