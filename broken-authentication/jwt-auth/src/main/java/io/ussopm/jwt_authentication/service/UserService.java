package io.ussopm.jwt_authentication.service;

import io.ussopm.jwt_authentication.dto.UserJson;
import io.ussopm.jwt_authentication.mapping.UserMapper;
import io.ussopm.jwt_authentication.model.User;
import io.ussopm.jwt_authentication.repository.UserRepository;
import io.ussopm.jwt_authentication.security.JwtIssuer;
import io.ussopm.jwt_authentication.security.Role;
import io.ussopm.jwt_authentication.utils.CustomException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtIssuer issuer;
    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

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

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new UserJson(
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
    private void createOrCheckAdminUser() {
        if (userRepository.findFirstByUsernameIgnoreCase("superadmin").isEmpty()) {
            User admin = new User();
            admin.setUsername("superadmin");
            admin.setFullName("ФИО");
            admin.setCreatedTime(new Date());
            admin.setPasswordUpdateTime(new Date());
            admin.setRole(String.valueOf(Role.SUPERADMIN));
            admin.setPassword(passwordEncoder.encode("srpo2023"));
            admin.setEmail("srpo@sts");
            userRepository.save(admin);
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

    // TODO -> VULNERABILITY? Retrieves plain user information with sensitive data like token, password
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    // TODO -> VULNERABILITY? Retrieves plain user information with sensitive data like token, password
    public User get(long id) {
        return this.userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("User with ID " + id + " not found"));
    }

    // TODO -> VULNERABILITY? user does not have authorities to create a user with higher privileges,
    //  but program does not check it [admin can not create a new superadmin]
    public void create(User userEntity) throws CustomException {
        if (userRepository.findByUsername(userEntity.getUsername()).isPresent()) {
            throw new CustomException(String.format("Логин %s уже используется", userEntity.getUsername()));
        }
        if (isValidPassword(userEntity.getPassword())) {
            User user = new User();
            user.setUsername(userEntity.getUsername());
            user.setFullName(userEntity.getFullName());
            user.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            user.setRole(userEntity.getRole());
            user.setEmail(userEntity.getEmail());
            user.setCreatedTime(new Date());
            user.setPasswordUpdateTime(new Date());
            this.userRepository.save(user);
        } else {
            throw new CustomException("Пароль должен быть от 8 символов, содержать заглавную букву, цифру и специальный символ.");
        }
    }

    private boolean isValidPassword(String password) {
        String PASSWORD_PATTERN =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }


    // TODO -> VULNERABILITY? user does not have authorities to edit a user with higher privileges,
    //  but program does not check it [admin can not edit a new superadmin] BROKEN AUTHORIZATION
    public void edit(User userJson) throws CustomException {
        User user = get(userJson.getId());
        user.setUsername(userJson.getUsername());
        String password = userJson.getPassword();
        if (!password.isEmpty() && !isValidPassword(password)) {
            throw new CustomException("Только латинские буквы и цифры,содержать хотя бы одну букву в верхнем регистре, одну в нижнем, одну цифру и один специальный символ");
        } else if (!password.isEmpty()){
            String encodedPassword = passwordEncoder.encode(userJson.getPassword());
            if (passwordEncoder.matches(user.getPassword(), encodedPassword)) {
                throw new CustomException("Пароли совпадают, введите уникальный");
            }
            user.setPassword(encodedPassword);
            user.setPasswordUpdateTime(new Date());
        }
        user.setEmail(userJson.getEmail());
        user.setRole(userJson.getRole());
        user.setFullName(userJson.getFullName());
        userRepository.save(user);
    }

    public void delete(long id) throws CustomException {
        User user = get(id);
        userRepository.delete(user);
    }



/////////////////////////// SPACE ///////////////////////////////////




    // TODO -> SOLUTION every method return DTO instead of Entity.
    //  Creating and Editing methods check for authorities to make actions
    public UserJson getUserById(long id) {
        return userMapper.mappingUserToUserJson(get(id));
    }

    public List<UserJson> getAllUsersSecurely() {
        return userMapper.mappingUserListToUserJsonList(getAllUsers());
    }

    public void createSecurely(User userEntity) throws CustomException {
        if (userRepository.findByUsername(userEntity.getUsername()).isPresent()) {
            throw new CustomException(String.format("Логин %s уже используется", userEntity.getUsername()));
        }
        if (userEntity.getRole().equalsIgnoreCase("superadmin")) {
            throw new CustomException("Отказано в доступе");
        }
        if (isValidPassword(userEntity.getPassword())) {
            User user = new User();
            user.setUsername(userEntity.getUsername());
            user.setFullName(userEntity.getFullName());
            user.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            user.setRole(userEntity.getRole());
            user.setEmail(userEntity.getEmail());
            user.setCreatedTime(new Date());
            user.setPasswordUpdateTime(new Date());
            this.userRepository.save(user);
        } else {
            throw new CustomException("Пароль должен быть от 8 символов, содержать заглавную букву, цифру и специальный символ.");
        }
    }

    public void editSecurely(User userJson) throws CustomException {
        User user = get(userJson.getId());
        user.setUsername(userJson.getUsername());
        String password = userJson.getPassword();
        if (user.getRole().equalsIgnoreCase("superadmin")) {
            throw new CustomException("Отказано в доступе");
        }
        if (!password.isEmpty() && !isValidPassword(password)) {
            throw new CustomException("Только латинские буквы и цифры,содержать хотя бы одну букву в верхнем регистре, одну в нижнем, одну цифру и один специальный символ");
        } else if (!password.isEmpty()){
            String encodedPassword = passwordEncoder.encode(userJson.getPassword());
            if (passwordEncoder.matches(user.getPassword(), encodedPassword)) {
                throw new CustomException("Пароли совпадают, введите уникальный");
            }
            user.setPassword(encodedPassword);
            user.setPasswordUpdateTime(new Date());
        }
        user.setEmail(userJson.getEmail());
        user.setRole(userJson.getRole());
        user.setFullName(userJson.getFullName());
        userRepository.save(user);
    }

    public void deleteSecurely(long id) throws CustomException {
        User user = get(id);
        if (user.getRole().equalsIgnoreCase("superadmin")) {
            throw new CustomException("Отказано в доступе");
        }
        userRepository.delete(user);
    }

}
