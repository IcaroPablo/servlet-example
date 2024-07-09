package com.example.common.interfaces.service;

import com.example.common.infrastructure.utils.FileUtils;
import com.example.common.interfaces.repositories.ShoppingRepositoryView;
import com.example.common.interfaces.repositories.repository.ShoppingRepository;
import com.example.common.interfaces.repositories.repository.UserRepository;
import com.example.common.interfaces.rest.dtos.CartDto;
import com.example.common.interfaces.rest.dtos.ProductDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

import static com.example.common.constants.Constants.BD_CART;
import static com.example.common.constants.Constants.BD_CART_CABECALHO;
import static com.example.common.infrastructure.utils.Present.println;

@AllArgsConstructor
public class ShoppingService {

    private ShoppingRepositoryView repository;
    private FileUtils fileUtils;

    public ShoppingService() {
        this.repository = new ShoppingRepository();
        FileUtils.createIfNotExists(BD_CART, BD_CART_CABECALHO);
        println("Construtor shopping service");
    }

    public boolean hasSavedCart(String cpf) throws IOException {
        return repository.hasSavedCart(cpf);
    }

    public boolean saveCart(String cpf, List<ProductDto> cart) throws IOException {
        return repository.saveCart(cpf, cart);
    }

    public boolean deleteCart(String cpf) {
        return repository.deleteCart(cpf);
    }

    public CartDto updateCartItem(String cpf, String codigo, Integer quantidade) {
        return repository.updateCartItem(cpf, codigo, quantidade);
    }

    public CartDto getCart(String cpf) {
        return repository.getCart(cpf);
    }
}
