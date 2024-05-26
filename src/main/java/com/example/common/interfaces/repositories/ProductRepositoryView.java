package com.example.common.interfaces.repositories;

import com.example.common.interfaces.rest.dtos.ProductDto;
import com.example.domain.entities.Product;

import java.util.List;

public interface ProductRepositoryView {

    boolean createProduct(Product product);

    boolean updateProduct(Product product);

    boolean deleteProduct(String code);

    ProductDto getProduct(String code);

    List<ProductDto> getAll();
}
