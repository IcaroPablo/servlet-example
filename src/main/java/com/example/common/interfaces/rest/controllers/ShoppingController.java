package com.example.common.interfaces.rest.controllers;

import com.example.common.interfaces.rest.dtos.CartDto;
import com.example.common.interfaces.rest.dtos.ProductDto;
import com.example.common.interfaces.service.ShoppingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

@AllArgsConstructor
public class ShoppingController extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();
    private ShoppingService shoppingService;
    private static final Logger logger = Logger.getLogger(ShoppingController.class.getName());


    public ShoppingController() {
        this.shoppingService = new ShoppingService();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null) {
            if (pathInfo.equals("/check")) {
                String cpf = req.getParameter("cpf");
                boolean carrinhoAberto = shoppingService.hasSavedCart(cpf);

                if (carrinhoAberto) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("true");
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Carrinho não encontrado");
                }
            } else if (pathInfo.equals("/get")) {
                String cpf = req.getParameter("cpf");
                CartDto carrinho = shoppingService.getCart(cpf);

                if (carrinho != null && !carrinho.getProducts().isEmpty()) {
                    String jsonResponse = objectMapper.writeValueAsString(carrinho);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.setContentType("application/json");
                    resp.getWriter().write(jsonResponse);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Carrinho não encontrado");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Recurso não encontrado");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Invalid request");
        }
    }


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/update")) {
            cartUpdate(req, resp);
            return;
        }

        CartDto cartDto = objectMapper.readValue(req.getInputStream(), CartDto.class);
        String cpf = cartDto.getCpf();
        List<ProductDto> carrinho = cartDto.getProducts();

        shoppingService.saveCart(cpf, carrinho);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("Carrinho salvo com sucesso!");
    }

    private void cartUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cpf = req.getParameter("cpf");
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(requestBody.toString());
        String codigo = jsonNode.has("code") ? jsonNode.get("code").asText() : null;
        Integer quantidade = jsonNode.has("quantity") ? jsonNode.get("quantity").asInt() : null;

        if (cpf == null || cpf.isEmpty() || codigo == null || codigo.isEmpty() || quantidade == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "É preciso enviar os dados corretos.");
            return;
        }

        CartDto cartDto = shoppingService.updateCartItem(cpf, codigo, quantidade);

        if (cartDto != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Carrinho atualizado com sucesso.");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Carrinho não encontrado.");
        }
    }


    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cpf = req.getParameter("cpf");

        shoppingService.deleteCart(cpf);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("Todos os itens do carrinho foram excluídos");
    }
}
