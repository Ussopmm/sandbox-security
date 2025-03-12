package io.ussopm.jwt_authentication.controller;

import io.ussopm.jwt_authentication.dto.UserJson;
import io.ussopm.jwt_authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping()
public class AuthenticationRestController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserJson userJson) {
        return this.userService.login(userJson);
    }
}
