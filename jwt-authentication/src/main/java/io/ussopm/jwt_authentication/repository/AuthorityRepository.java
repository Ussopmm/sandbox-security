package io.ussopm.jwt_authentication.repository;

import io.ussopm.jwt_authentication.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    Optional<Authority> findFirstByRoleIgnoreCase(String authority);

    Optional<Authority> findByRole(String authority);
}
