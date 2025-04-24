package io.ussopm.jwt_authentication.mapping;

import io.ussopm.jwt_authentication.dto.ProductDTO;
import io.ussopm.jwt_authentication.model.Product;

import java.util.List;

public interface ProductMapper {

    ProductDTO mappingProductToProductDTO(Product product);
    List<ProductDTO> mappingProductsToProductDTOs(List<Product> products);
}
