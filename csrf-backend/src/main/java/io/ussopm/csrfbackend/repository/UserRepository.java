package io.ussopm.csrfbackend.repository;

import io.ussopm.csrfbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
