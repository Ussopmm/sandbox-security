package io.ussopm.jwt_authentication.repository;

import io.ussopm.jwt_authentication.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
