package com.example.domain.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Product {
    private String code;
    private String description;
    private Double price;
    private Integer quantity;
}
