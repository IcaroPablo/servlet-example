package com.example.domain.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cart {
    private String codigo;
    private String descricao;
    private Double valor;
    private int quantidade;
}
