package com.example.common.interfaces.rest.dtos;

import com.example.domain.entities.Product;
import com.example.domain.entities.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private String code;
    private String description;
    private Double price;
    private Integer quantity;

    public static ProductDto fromProduct(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setCode(product.getCode());
        productDto.setDescription(product.getDescription());
        productDto.setPrice(product.getPrice());
        productDto.setQuantity(product.getQuantity());

        return productDto;
    }
}
