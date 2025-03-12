package io.ussopm.jwt_authentication.service;

import io.ussopm.jwt_authentication.dto.AuthorizedUser;
import io.ussopm.jwt_authentication.dto.UserJson;
import io.ussopm.jwt_authentication.model.Authority;
import io.ussopm.jwt_authentication.model.User;
import io.ussopm.jwt_authentication.repository.AuthorityRepository;
import io.ussopm.jwt_authentication.repository.UserRepository;
import io.ussopm.jwt_authentication.security.JwtIssuer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtIssuer issuer;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    public ResponseEntity<?> login(UserJson userJson) {
        String username = userJson.getUsername();
        String password = userJson.getPassword();
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));


        if (user.getToken() != null && !user.getToken().isEmpty() && user.getToken() != "") {
            user.setToken(null);
            this.userRepository.save(user);
        }
        if (isLocked(user)) {
            throw new BadCredentialsException("Your account has been locked due to 5 failed attempts. It will be unlocked after 5 minutes.");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = issuer.issue(authentication);
            user.setToken(token);
            userRepository.save(user);

            resetFailedAttempts(username);
            if (isPasswordExpired(user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "Password expired", "userId", user.getId()));
            }

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new AuthorizedUser(
                    token,
                    username,
                    user.getRole()));
        } catch (BadCredentialsException e) {
            increaseFailedAttempts(user);
            if (user.getFailedAttempts() >= 5) {
                lockUser(user);
                throw new BadCredentialsException("Your account has been locked due to 5 failed attempts. It will be unlocked after 5 minutes.");
            }
            throw new BadCredentialsException("Invalid username or password");
        }
    }


    @PostConstruct
    private void initRoles() {
        if (authorityRepository.findFirstByRoleIgnoreCase("ROLE_USER").isEmpty()) {
            Authority authority = new Authority();
            authority.setRole("ROLE_USER");
            authorityRepository.save(authority);
        }
        if (authorityRepository.findFirstByRoleIgnoreCase("ROLE_ADMIN").isEmpty()) {
            Authority authority = new Authority();
            authority.setRole("ROLE_ADMIN");
            authorityRepository.save(authority);
        }
    }

    @PostConstruct
    private void initNewUser() {
        if (userRepository.findFirstByUsernameIgnoreCase("admin").isEmpty()) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("Admin1234!"));
            user.setRole(authorityRepository.findByRole("ROLE_ADMIN").get().getRole());
            user.setEmail("admin@gmail.com");
            this.userRepository.save(user);
        }
    }

    public void increaseFailedAttempts(User user) {
        int newFailedAttempts = user.getFailedAttempts()+1;
        user.setFailedAttempts(newFailedAttempts);
        userRepository.save(user);
    }

    public void resetFailedAttempts(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user != null) {
            user.setFailedAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);
        }
    }
    public void lockUser(User user) {
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    public boolean isLocked(User user) {
        if (user.getLockTime() == null) {
            return false;
        }

        Instant lockTime = user.getLockTime().toInstant(); // Convert Date to Instant
        Instant now = Instant.now();

        // Add 5 minutes to lockTime and check if it's before now
        if (lockTime.plusSeconds(5 * 60).isBefore(now)) {
            resetFailedAttempts(user.getUsername());
            return false;
        }
        return true;
    }

    public boolean isPasswordExpired(User user) {
        if (!user.getUsername().equals("superadmin")) {
            Instant passwordLastUpdated = user.getPasswordUpdateTime().toInstant();
            Instant now = Instant.now();

            long daysBetween = ChronoUnit.DAYS.between(passwordLastUpdated, now);

            return daysBetween > 30;
        } else {
            return false;
        }
    }
}
