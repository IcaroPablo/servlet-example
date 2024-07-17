package com.example.common.interfaces.rest.controllers;

import com.example.common.interfaces.rest.dtos.ProductDto;
import com.example.common.interfaces.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.common.infrastructure.utils.Present.printf;

@AllArgsConstructor
public class ProductController extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Gson gson = new Gson();
    private ProductService productService;
    private static final Logger logger = Logger.getLogger(ProductController.class.getName());

    public ProductController() {
        this.productService = new ProductService();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.equals("/update")) {
            productUpdate(req, resp);
            return;
        }

        ProductDto productDto = objectMapper.readValue(req.getInputStream(), ProductDto.class);

        boolean isCreated = productService.createProduct(productDto);

        if (isCreated) {
            logger.log(Level.INFO, "Produto cadastrado: {0}", new Object[]{productDto.getCode()});
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("Produto cadastrado com sucesso.");
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Falha ao cadastrar produto.");
        }
    }

    private void productUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProductDto productDto = objectMapper.readValue(req.getInputStream(), ProductDto.class);

        if (productDto.getCode() == null || productDto.getCode().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "É preciso enviar os dados corretos.");
            return;
        }

        logger.log(Level.INFO, "Atualizando produto: {0}", new Object[]{productDto.getCode()});
        boolean isUpdated = productService.updateProduct(productDto);

        if (isUpdated) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Produto atualizado com sucesso.");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falha ao atualizar o produto.");
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null) {
            if (pathInfo.equals("/get-product")) {
                printf("Endpoint /get-product. Code = %s", req.getParameter("code"));
                logger.log(Level.INFO, "Endpoint /get-product. Code = {0}", new Object[]{req.getParameter("code")});
                String code = req.getParameter("code");
                ProductDto productDto = productService.getProduct(code);

                if (productDto != null) {
                    String jsonProduct = gson.toJson(productDto);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.setContentType("application/json");
                    resp.getWriter().write(jsonProduct);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado");
                    logger.log(Level.WARNING, "Produto não encontrado");
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
                    logger.log(Level.WARNING, "Não há produtos cadastrados");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Recurso não encontrado");
                logger.log(Level.WARNING, "Recurso não encontrado");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Requisição inválida");
            logger.log(Level.WARNING, "Requisição inválida: path info está nulo.");
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codigo = req.getParameter("code");

        if (productService.deleteProduct(codigo)) {
            logger.log(Level.INFO, "Produto excluído: {0}", new Object[]{codigo});
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Produto excluído com sucesso.");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Falha ao excluir o produto.");
        }
    }
}
