package com.example.common.interfaces.rest.controllers;

import com.example.common.interfaces.rest.dtos.CartDto;
import com.example.common.interfaces.service.ShoppingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class ShoppingController extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Gson gson = new Gson();
    private ShoppingService shoppingService;

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
                List<CartDto> carrinho = shoppingService.getCart(cpf);

                if (carrinho != null && !carrinho.isEmpty()) {
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
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String cpf = req.getParameter("cpf");
        String carrinhoJson = req.getParameter("carrinho");

        List<CartDto> carrinho = gson.fromJson(carrinhoJson, List.class);
        shoppingService.saveCart(cpf, carrinho);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write("Carrinho salvo com sucesso!");
    }

    @Override
    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codigo = req.getParameter("codigo");
        Integer quantidade = Integer.parseInt(req.getParameter("quantidade"));
        String carrinhoJson = req.getParameter("carrinho");

        List<CartDto> carrinho = gson.fromJson(carrinhoJson, List.class);

        boolean isUpdated = shoppingService.updateCartItem(carrinho, codigo, quantidade);

        if (isUpdated) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Produto atualizado com sucesso.");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falha ao atualizar o produto.");
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
