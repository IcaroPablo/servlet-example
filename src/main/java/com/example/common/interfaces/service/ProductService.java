package com.example.common.interfaces.service;

import com.example.common.infrastructure.utils.FileUtils;
import com.example.common.interfaces.repositories.ProductRepositoryView;
import com.example.common.interfaces.rest.dtos.ProductDto;
import com.example.domain.entities.Product;

import java.util.List;
import java.util.Map;

import static com.example.common.constants.Constants.BD_PRODUCT;
import static com.example.common.constants.Constants.BD_PRODUCT_CABECALHO;

public class ProductService {
    private ProductRepositoryView repository;
    Product product = new Product();

    private ProductService(FileUtils fileUtils) {
        fileUtils.createIfNotExists(BD_PRODUCT, BD_PRODUCT_CABECALHO);
    }

    public boolean createProduct(ProductDto productDto) {
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setQuantity(productDto.getQuantity());
        product.setCode(productDto.getCode());

        return repository.createProduct(product);
    }

    public ProductDto getProduct(String code) {
        return repository.getProduct(code);
    }

    public List<ProductDto> getAll() {
        return repository.getAll();
    }

    public boolean deleteProduct(String code) {
        return repository.deleteProduct(code);
    }

    public boolean updateProduct(Map<String, String> params) {
        if (params.containsKey("description")) {
            product.setDescription(params.get("description"));
        }
        if (params.containsKey("price")) {
            product.setPrice(Double.parseDouble(params.get("price")));
        }
        if (params.containsKey("quantity")) {
            product.setQuantity(Integer.parseInt(params.get("quantity")));
        }
        if (params.containsKey("code")) {
            product.setCode(params.get("code"));
        }

        return repository.updateProduct(product);
    }
}
