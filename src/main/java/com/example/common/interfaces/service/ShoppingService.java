package com.example.common.interfaces.service;

import com.example.common.infrastructure.utils.FileUtils;
import com.example.common.interfaces.repositories.ShoppingRepositoryView;
import com.example.common.interfaces.rest.dtos.CartDto;

import java.util.List;

import static com.example.common.constants.Constants.BD_CART;
import static com.example.common.constants.Constants.BD_CART_CABECALHO;

public class ShoppingService {

    private ShoppingRepositoryView repository;

    private ShoppingService(FileUtils fileUtils) {
        fileUtils.createIfNotExists(BD_CART, BD_CART_CABECALHO);
    }

    public boolean hasSavedCart(String cpf) {
        return repository.hasSavedCart(cpf);
    }

    public boolean saveCart(String cpf, List<CartDto> cart) {
        return repository.saveCart(cpf, cart);
    }

    public boolean deleteCart(String cpf) {
        return repository.deleteCart(cpf);
    }

    public boolean updateCartItem(List<CartDto> cart, String codigo, Integer quantidade) {
        return repository.updateCartItem(cart, codigo, quantidade);
    }

    public List<CartDto> getCart(String cpf) {
        return repository.getCart(cpf);
    }

    public boolean getAllCarts() {
        return true;
    }
}
