package io.ussopm.jwt_authentication.mapping.impl;

import io.ussopm.jwt_authentication.dto.UserJson;
import io.ussopm.jwt_authentication.mapping.UserMapper;
import io.ussopm.jwt_authentication.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public List<UserJson> mappingUserListToUserJsonList(List<User> users) {
        return users.stream().map(this::mappingUserToUserJson).toList();
    }

    @Override
    public UserJson mappingUserToUserJson(User user) {
        return UserJson.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .created(String.valueOf(user.getCreatedTime()))
                .email(user.getEmail())
                .build();
    }

    @Override
    public User mappingUserJsonToUser(UserJson user) {
        return User.builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}
