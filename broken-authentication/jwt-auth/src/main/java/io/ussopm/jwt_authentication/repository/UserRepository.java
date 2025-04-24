package io.ussopm.jwt_authentication.repository;

import io.ussopm.jwt_authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsername(String username);

    Optional<User> findFirstByUsernameIgnoreCase(String username);
}
