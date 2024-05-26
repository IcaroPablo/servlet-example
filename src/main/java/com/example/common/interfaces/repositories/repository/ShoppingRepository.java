package com.example.common.interfaces.repositories.repository;

import com.example.common.interfaces.repositories.ShoppingRepositoryView;
import com.example.common.interfaces.rest.dtos.CartDto;
import com.example.domain.entities.Cart;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.common.constants.Constants.BD_CART;
import static com.example.common.constants.Constants.ERROR_TEMP_FILE;
import static com.example.common.infrastructure.utils.Present.print;
import static com.example.common.infrastructure.utils.Present.printf;
import static com.example.common.interfaces.rest.dtos.CartDto.fromCart;

public class ShoppingRepository implements ShoppingRepositoryView {

    Cart cart = new Cart();

    @Override
    public boolean hasSavedCart(String cpf) {
        return !getCart(cpf).isEmpty();
    }

    @Override
    public boolean saveCart(String cpf, List<CartDto> cart) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BD_CART, true))) {
            StringBuilder linha = new StringBuilder(cpf + ",aberto");
            for (CartDto item : cart) {
                linha.append(",").append(item.getCodigo()).append(":")
                        .append(item.getDescricao()).append(":")
                        .append(item.getValor()).append(":")
                        .append(item.getQuantidade());
            }
            writer.write(linha.toString());
            writer.newLine();
            return true;
        } catch (IOException e) {
            printf("Exception: %s", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCart(String cpf) {
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_CART));
             BufferedWriter writer = new BufferedWriter(new FileWriter("temp_carrinho.txt"))) {

            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length >= 3 && cpf.equals(dados[0].trim()) && "aberto".equals(dados[1].trim())) {
                    continue;
                }
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            printf("Exception: %s", e.getMessage());
        }

        File fileCarrinho = new File(BD_CART);
        File tempFileCarrinho = new File("temp_carrinho.txt");
        if (!tempFileCarrinho.renameTo(fileCarrinho)) {
            print(ERROR_TEMP_FILE);
            return false;
        }

        print("Carrinho limpo com sucesso. \n");
        return true;
    }

    @Override
    public boolean updateCartItem(List<CartDto> cart, String code, Integer quantity) {
        if(cart.isEmpty()) {
            print("O carrinho está vazio");
            return false;
        }
        for (int i = 0; i < cart.size(); i++) {
            CartDto item = cart.get(i);
            if (item.getCodigo().equals(code)) {
                if (quantity == 0) {
                    cart.remove(i);
                    i--;
                } else {
                    item.setQuantidade(quantity);
                }
            }
        }
        return true;
    }

    @Override
    public List<CartDto> getCart(String cpf) {
        List<CartDto> carrinhoItens = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_CART))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(cpf) && parts[1].equals("aberto")) {
                    String[] itemParts = parts[2].split(",");
                    for (String itemPart : itemParts) {
                        String[] itemValues = itemPart.split(":");
                        cart.setCodigo(itemValues[0]);
                        cart.setDescricao(itemValues[1]);
                        cart.setValor(Double.parseDouble(itemValues[2]));
                        cart.setQuantidade(Integer.parseInt(itemValues[3]));
                        carrinhoItens.add(fromCart(cart));
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return carrinhoItens;
    }

    @Override
    public boolean getAllCarts() {
        return false;
    }
}
