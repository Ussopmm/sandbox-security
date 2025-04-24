package io.ussopm.jwt_authentication.service;

import io.ussopm.jwt_authentication.dto.ProductDTO;
import io.ussopm.jwt_authentication.mapping.ProductMapper;
import io.ussopm.jwt_authentication.model.Product;
import io.ussopm.jwt_authentication.repository.ProductRepository;
import io.ussopm.jwt_authentication.utils.CustomException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {

    ProductRepository productRepository;
    ProductMapper productMapper;

    public List<Product> getAll(){
        return productRepository.findAll();
    }

    public List<ProductDTO> getAllSecurely(){
        return productMapper.mappingProductsToProductDTOs(getAll());
    }


    public Product get(long id) throws CustomException {
        return productRepository.findById(id).orElseThrow(() -> new CustomException("Product not found"));
    }

    public ProductDTO getById(long id) throws CustomException {
        return productMapper.mappingProductToProductDTO(get(id));
    }


    public void create(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        productRepository.save(product);
    }

    public void edit(ProductDTO productDTO) throws CustomException {
        Product product = get(productDTO.getId());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        productRepository.save(product);
    }

    public void delete(long id) {
        productRepository.deleteById(id);
    }
}
