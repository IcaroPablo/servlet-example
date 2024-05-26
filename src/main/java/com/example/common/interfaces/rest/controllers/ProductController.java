package com.example.common.interfaces.rest.controllers;

import com.example.common.interfaces.rest.dtos.ProductDto;
import com.example.common.interfaces.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class ProductController extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Gson gson = new Gson();
    private ProductService productService;
    ProductDto productDto = new ProductDto();

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        productDto.setCode(req.getParameter("codigo"));
        productDto.setDescription(req.getParameter("descricao"));
        productDto.setPrice(Double.parseDouble(req.getParameter("valor")));
        productDto.setQuantity(Integer.parseInt(req.getParameter("quantidade")));

        boolean isCreated = productService.createProduct(productDto);

        if (isCreated) {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("Produto cadastrado com sucesso.");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao cadastrar produto.");
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null) {
            if (pathInfo.equals("/product")) {
                String codigo = req.getParameter("codigo");
                productDto = productService.getProduct(codigo);

                if (productDto != null) {
                    String jsonProduct = gson.toJson(productDto);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.setContentType("application/json");
                    resp.getWriter().write(jsonProduct);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado");
                }
            } else if (pathInfo.equals("/products")) {
                var products = productService.getAll();
                if (products != null && !products.isEmpty()) {
                    String jsonResponse = objectMapper.writeValueAsString(products);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.setContentType("application/json");
                    resp.getWriter().write(jsonResponse);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Não há produtos cadastrados");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Recurso não encontrado");
            }
        }
    }

    @Override
    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, String[]> parameterMap = req.getParameterMap();
        Map<String, String> updates = parameterMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

        boolean isUpdated = productService.updateProduct(updates);

        if (isUpdated) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Produto atualizado com sucesso.");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falha ao atualizar o produto.");
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codigo = req.getParameter("codigo");

        if (productService.deleteProduct(codigo)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Produto excluído com sucesso.");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falha ao excluir o produto.");
        }
    }
}
