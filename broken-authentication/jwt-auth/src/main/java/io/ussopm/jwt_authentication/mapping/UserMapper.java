package io.ussopm.jwt_authentication.mapping;

import io.ussopm.jwt_authentication.dto.UserJson;
import io.ussopm.jwt_authentication.model.User;

import java.util.List;

public interface UserMapper {

    List<UserJson> mappingUserListToUserJsonList(List<User> users);
    UserJson mappingUserToUserJson(User user);
    User mappingUserJsonToUser(UserJson user);
}
