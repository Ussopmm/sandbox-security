package io.ussopm.jwt_authentication.mapping.impl;

import io.ussopm.jwt_authentication.dto.ProductDTO;
import io.ussopm.jwt_authentication.mapping.ProductMapper;
import io.ussopm.jwt_authentication.mapping.UserMapper;
import io.ussopm.jwt_authentication.model.Product;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductMapperImpl implements ProductMapper {

    UserMapper userMapper;

    @Override
    public ProductDTO mappingProductToProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .user(userMapper.mappingUserToUserJson(product.getUser()))
                .build();
    }

    @Override
    public List<ProductDTO> mappingProductsToProductDTOs(List<Product> products) {
        return products.stream().map(this::mappingProductToProductDTO).toList();
    }
}
