package io.ussopm.jwt_authentication.controller;

import io.ussopm.jwt_authentication.model.User;
import io.ussopm.jwt_authentication.service.UserService;
import io.ussopm.jwt_authentication.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/secure")
public class SecureUserRestController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok().body(this.userService.getAllUsersSecurely());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(this.userService.getUserById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка при получении данных");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody User userJson) {
        try {
            userService.createSecurely(userJson);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @PatchMapping("/edit")
    public ResponseEntity<?> edit (@RequestBody User userJson) {
        try {
            userService.editSecurely(userJson);
            return ResponseEntity.ok().build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete (@PathVariable long id) {
        try {
            userService.deleteSecurely(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка при удалении данных");
        }
    }
}
