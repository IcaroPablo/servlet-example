package com.example.common.interfaces.repositories.repository;

import com.example.common.interfaces.repositories.ShoppingRepositoryView;
import com.example.common.interfaces.rest.dtos.CartDto;
import com.example.common.interfaces.rest.dtos.ProductDto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static com.example.common.constants.Constants.BD_CART;
import static com.example.common.constants.Constants.ERROR_TEMP_FILE;
import static com.example.common.infrastructure.utils.Present.print;

public class ShoppingRepository implements ShoppingRepositoryView {

    private static final Logger logger = Logger.getLogger(ShoppingRepository.class.getName());

    @Override
    public boolean hasSavedCart(String cpf) {
        var cartDto = getCart(cpf);

        return cartDto.getProducts() != null && !cartDto.getProducts().isEmpty();
    }

    @Override
    public boolean saveCart(String cpf, List<ProductDto> cart) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BD_CART, true))) {
            StringBuilder linha = new StringBuilder(cpf + ",aberto,");
            for (ProductDto item : cart) {
                linha.append(item.getCode()).append(":")
                        .append(item.getDescription()).append(":")
                        .append(item.getPrice()).append(":")
                        .append(item.getQuantity()).append(";");
            }
            writer.write(linha.toString());
            writer.newLine();
            logger.log(Level.INFO, "Carrinho salvo. CPF {0}", new Object[]{cpf});
            return true;
        } catch (IOException e) {
            logger.log(Level.WARNING,"Exception: {0}", new Object[]{e.getMessage()});
            return false;
        }
    }

    private boolean updateCart(String cpf, List<ProductDto> cart) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(BD_CART), StandardCharsets.UTF_8);

            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.startsWith(cpf))
                    .collect(Collectors.toList());

            StringBuilder newLine = new StringBuilder(cpf + ",aberto,");
            for (ProductDto item : cart) {
                newLine.append(item.getCode()).append(":")
                        .append(item.getDescription()).append(":")
                        .append(item.getPrice()).append(":")
                        .append(item.getQuantity()).append(";");
            }

            updatedLines.add(newLine.toString());

            Files.write(Paths.get(BD_CART), updatedLines, StandardCharsets.UTF_8);

            logger.log(Level.INFO, "Carrinho salvo e atualizado para o CPF: {}", cpf);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Falha ao salvar o carrinho para o CPF {}. Exception: {}", new Object[]{ cpf, e.getMessage()});
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
                logger.log(Level.INFO,"Carrinho excluído. CPF {0}", new Object[]{cpf});
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Exception: {0}", new Object[]{e.getMessage()});
        }

        File fileCarrinho = new File(BD_CART);
        File tempFileCarrinho = new File("temp_carrinho.txt");
        if (!tempFileCarrinho.renameTo(fileCarrinho)) {
            print(ERROR_TEMP_FILE);
            return false;
        }

        logger.log(Level.INFO, "Carrinho limpo com sucesso e arquivo recriado.");
        return true;
    }

    @Override
    public CartDto updateCartItem(String cpf, String code, Integer quantity) {
        CartDto cartDto = getCart(cpf);

        logger.log(Level.INFO, "Recuperou o carrinho para o CPF: {}", cpf);

        if (cartDto == null || cartDto.getProducts().isEmpty()) {
            logger.log(Level.WARNING, "Carrinho não encontrado para o CPF: {}", cpf);
            return null;
        }

        List<ProductDto> cartItems = cartDto.getProducts();

        boolean found = false;
        for (ProductDto item : cartItems) {
            if (item.getCode().equals(code)) {
                item.setQuantity(quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            logger.log(Level.WARNING, "Produto com código {} não encontrado no carrinho do CPF: {}", new Object[]{ code, cpf});
            return null;
        }

        if (updateCart(cpf, cartItems)) {
            logger.log(Level.INFO, "Carrinho atualizado e salvo com sucesso para o CPF: {}", cpf);
            return cartDto;
        } else {
            logger.log(Level.WARNING, "Falha ao salvar o carrinho atualizado para o CPF: {}", cpf);
            return null;
        }
    }


    @Override
    public CartDto getCart(String cpf) {
        CartDto cartDto = new CartDto();
        cartDto.setCpf(cpf);
        List<ProductDto> carrinhoItens = new ArrayList<>();
        cartDto.setProducts(carrinhoItens);

        try (BufferedReader reader = new BufferedReader(new FileReader(BD_CART))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 3);
                if (parts.length >= 3 && parts[0].equals(cpf) && parts[1].equals("aberto")) {
                    String[] itemParts = parts[2].split(";");
                    for (String itemPart : itemParts) {
                        String[] itemValues = itemPart.split(":");
                        if (itemValues.length == 4) {
                            ProductDto productDto = new ProductDto();
                            productDto.setCode(itemValues[0]);
                            productDto.setDescription(itemValues[1]);
                            productDto.setPrice(Double.parseDouble(itemValues[2]));
                            productDto.setQuantity(Integer.parseInt(itemValues[3]));
                            carrinhoItens.add(productDto);
                            logger.log(Level.INFO, "Produto adicionado: Código: {0} | Descrição: {1} | Valor: R$ {2} | Qtd: {3}",
                                    new Object[]{itemValues[0], itemValues[1], itemValues[2], itemValues[3]});
                        } else {
                            logger.log(Level.WARNING, "Formato de item inválido: {0}", new Object[]{itemPart});
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Recuperando carrinho aberto para o CPF: {0}", new Object[]{cpf});
        logger.log(Level.INFO, "Total de itens no carrinho: {0}", new Object[]{carrinhoItens.size()});
        return cartDto;
    }
}
