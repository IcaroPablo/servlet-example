package com.example.common.interfaces.repositories.repository;

import com.example.common.interfaces.repositories.ProductRepositoryView;
import com.example.common.interfaces.rest.dtos.ProductDto;
import com.example.domain.entities.Product;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.common.constants.Constants.BD_PRODUCT;
import static com.example.common.constants.Constants.ERROR_CREATE_PRODUCT;
import static com.example.common.constants.Constants.ERROR_DELETE_PRODUCT;
import static com.example.common.constants.Constants.ERROR_RETRIEVE_PRODUCTS;
import static com.example.common.constants.Constants.ERROR_TEMP_FILE;
import static com.example.common.constants.Constants.ERROR_UPDATE_PRODUCT;
import static com.example.common.constants.Constants.SUCCESS_UPDATE;
import static com.example.common.infrastructure.utils.Present.print;
import static com.example.common.infrastructure.utils.Present.printf;
import static com.example.common.interfaces.rest.dtos.ProductDto.fromProduct;

public class ProductRepository implements ProductRepositoryView {
    Product product = new Product();

    @Override
    public boolean createProduct(Product product) {
        boolean createdProduct = false;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BD_PRODUCT, true))) {
            writer.write(product.getCode() + "," + product.getDescription() + "," + product.getPrice() + "," + product.getQuantity());
            writer.newLine();
            createdProduct = true;
            return createdProduct;
        } catch (IOException e) {
            print(ERROR_CREATE_PRODUCT);
        }
        return createdProduct;
    }

    @Override
    public ProductDto getProduct(String code) {
        ProductDto produtoDto = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_PRODUCT))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] dados = line.split(",");
                if (dados[0].equals(code)) {
                    product.setDescription(dados[1]);
                    product.setPrice(Double.parseDouble(dados[2]));
                    product.setQuantity(Integer.parseInt(dados[3]));
                    return fromProduct(product);
                }
            }
        } catch (IOException e) {
            printf("Exception: %s", e.getMessage());
        }
        return produtoDto;
    }

    @Override
    public List<ProductDto> getAll() {
        List<ProductDto> produtos = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_PRODUCT))) {
            reader.readLine();
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length >= 3) {
                    product.setCode(dados[0]);
                    product.setDescription(dados[1]);
                    product.setPrice(Double.parseDouble(dados[2]));
                    product.setQuantity(Integer.parseInt(dados[3]));
                    produtos.add(fromProduct(product));
                }
            }
        } catch (IOException e) {
            print(ERROR_RETRIEVE_PRODUCTS);
        }
        return produtos;
    }

    @Override
    public boolean updateProduct(Product product) {
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_PRODUCT));
             BufferedWriter writer = new BufferedWriter(new FileWriter("temp.txt"))) {

            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length >= 4 && product.getCode().equals(dados[0])) {
                    if (product.getDescription() != null) {
                        dados[1] = product.getDescription();
                    }
                    if (product.getPrice() != null) {
                        dados[2] = product.getPrice().toString();
                    }
                    if (product.getQuantity() != null) {
                        dados[3] = product.getQuantity().toString();
                    }
                }
                writer.write(String.join(",", dados));
                writer.newLine();
            }
        } catch (IOException e) {
            print(ERROR_UPDATE_PRODUCT);
        }

        File file = new File(BD_PRODUCT);
        File tempFile = new File("temp.txt");
        if (tempFile.renameTo(file)) {
            print(SUCCESS_UPDATE);
            return true;
        } else {
            print(ERROR_TEMP_FILE);
            return false;
        }
    }

    @Override
    public boolean deleteProduct(String code) {
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_PRODUCT));
             BufferedWriter writer = new BufferedWriter(new FileWriter("temp_bd.txt"))) {

            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length >= 4 && code.equals(dados[0].trim())) {
                    continue;
                }
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            print(ERROR_DELETE_PRODUCT);
            return false;
        }

        File fileBD = new File(BD_PRODUCT);
        File tempFileBD = new File("temp_bd.txt");
        if (!tempFileBD.renameTo(fileBD)) {
            print(ERROR_TEMP_FILE);
            return false;
        }
        return true;
    }
}
