package com.example.common.interfaces.repositories;

import com.example.common.interfaces.rest.dtos.CartDto;

import java.util.List;

public interface ShoppingRepositoryView {

    boolean hasSavedCart(String cpf);

    boolean saveCart(String cpf, List<CartDto> cart);

    boolean deleteCart(String cpf);

    boolean updateCartItem(List<CartDto> cart, String codigo, Integer quantidade);

    List<CartDto> getCart(String cpf);

    boolean getAllCarts();
}
