package io.ussopm.basicauthentication.service;

import io.ussopm.basicauthentication.dto.UserJson;
import io.ussopm.basicauthentication.model.User;
import io.ussopm.basicauthentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    public UUID KEY = null;

    public User register(UserJson userJson) {
        return this.userRepository.save(new User(userJson.getUsername(), userJson.getPassword(), new Date(), "USER"));
    }

    // vulnerable for brute force attack
    // do not have any password policy
    // insecure credential storage
    // does not have lockout mechanisms
    // no authorization, unprotected endpoints
    // show them how is password saved in database
    public String login(UserJson userJson) {
        User user = this.userRepository.findByUsername(userJson.getUsername());
        if (user.getPassword().equals(userJson.getPassword())) {
            this.KEY = UUID.randomUUID();
            return KEY.toString();
        }
        return null;
    }

    public List<User> getAll(String key) throws BadRequestException {
        if (key.equals(this.KEY.toString())) {
            return this.userRepository.findAll();
        }
        throw new BadRequestException();
    }
}
