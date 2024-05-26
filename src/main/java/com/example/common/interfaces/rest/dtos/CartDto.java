package com.example.common.interfaces.rest.dtos;


import com.example.domain.entities.Cart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private String codigo;
    private String descricao;
    private Double valor;
    private int quantidade;

    public static CartDto fromCart(Cart cart) {
        return new CartDto(cart.getCodigo(), cart.getDescricao(), cart.getValor(), cart.getQuantidade());
    }
}
