package io.ussopm.basicauthentication.controller;

import io.ussopm.basicauthentication.dto.UserJson;
import io.ussopm.basicauthentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody UserJson userJson) {
        return ResponseEntity.ok(this.userService.register(userJson));
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserJson userJson) {
        return ResponseEntity.ok(this.userService.login(userJson));
    }

    @GetMapping("getAll")
    public ResponseEntity<?> getAll(@RequestParam String key) {
        try {
            return ResponseEntity.ok(this.userService.getAll(key));
        } catch (Exception e) {
            
            return ResponseEntity.badRequest().body("Forbidden");
        }
    }
}


