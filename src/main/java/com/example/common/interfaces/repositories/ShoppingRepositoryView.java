package com.example.common.interfaces.repositories;

import com.example.common.interfaces.rest.dtos.CartDto;
import com.example.common.interfaces.rest.dtos.ProductDto;

import java.util.List;

public interface ShoppingRepositoryView {

    boolean hasSavedCart(String cpf);

    boolean saveCart(String cpf, List<ProductDto> cart);

    boolean deleteCart(String cpf);

    CartDto updateCartItem(String cpf, String codigo, Integer quantidade);

    CartDto getCart(String cpf);
}
