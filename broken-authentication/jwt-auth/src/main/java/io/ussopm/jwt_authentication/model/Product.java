package io.ussopm.jwt_authentication.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "t_product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    Double price;
    String description;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;

}
