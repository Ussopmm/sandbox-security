package io.ussopm.jwt_authentication.dto;

import io.ussopm.jwt_authentication.model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    Long id;
    String name;
    Double price;
    String description;
    UserJson user;
}
