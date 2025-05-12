package io.ussopm.jwt_authentication.controller;

import io.ussopm.jwt_authentication.dto.ProductDTO;
import io.ussopm.jwt_authentication.model.User;
import io.ussopm.jwt_authentication.service.ProductService;
import io.ussopm.jwt_authentication.utils.CustomException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/product")
public class ProductRestController {

    ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        // TODO -> INCORRECT PRODUCT RETURN [IT RETURN ENTITY INSTEAD OF DTO AND IT CONTAINS USER WITH SENSITIVE DATA]
        try {
//            return ResponseEntity.ok().body(this.productService.getAll());

            // TODO -> CORRECT IMPLEMENTATION [uses mapping to map entity into dto and it contains user dto]
            return ResponseEntity.ok().body(this.productService.getAllSecurely());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUserById(@PathVariable long id) {
        // TODO -> INCORRECT PRODUCT RETURN [IT RETURN ENTITY INSTEAD OF DTO AND IT CONTAINS USER WITH SENSITIVE DATA]
        try {
            return ResponseEntity.ok().body(this.productService.get(id));

            // TODO -> CORRECT IMPLEMENTATION [uses mapping to map entity into dto and it contains user dto]
//            return ResponseEntity.ok().body(this.productService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка при получении данных");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProductDTO productDTO) {
        try {
            productService.create(productDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @PatchMapping("/edit")
    public ResponseEntity<?> edit (@RequestBody ProductDTO productDTO) {
        try {
            productService.edit(productDTO);
            return ResponseEntity.ok().build();
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete (@PathVariable long id) {
        try {
            productService.delete(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка при удалении данных");
        }
    }
}
