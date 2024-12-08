package com.example.common.constants;

import static com.example.common.config.AppConfig.properties;

public class Constants {
//    public static final String BD_USERS = properties.getProperty("bd_users_access");
    public static final String BD_USERS = "/Users/joao/Documents/repos-jv/bd-lojinha/bd_users_access.csv";
    public static final String BD_USERS_INFORMATION = "/Users/joao/Documents/repos-jv/bd-lojinha/bd_users_information.csv";
    public static final String BD_USERS_INFORMATION_CABECALHO = "CPF, USERNAME, PHONE, IS_ADMINISTRADOR";
    public static final String BD_USERS_CABECALHO = "CPF,PASSWORD,IS_ADMINISTRADOR";
    public static final String BD_PRODUCT = "/Users/joao/Documents/repos-jv/bd-lojinha/bd_product.csv";
    public static final String BD_CART = "/Users/joao/Documents/repos-jv/bd-lojinha/carrinho_compras.csv";
    public static final String BD_PRODUCT_CABECALHO = "CODE, DESCRIPTION, PRICE, QUANTITY";
    public static final String BD_CART_CABECALHO = "CPF, STATUS, ITENS";
    public static final String ERROR_READ_FILE = "Houve um erro ao tentar ler os dados do arquivo";
    public static final String ERROR_CREATE_USER = "Erro ao tentar criar cadastro de usuário";
    public static final String ERROR_CREATE_PRODUCT = "Erro ao cadastrar produto";
    public static final String ERROR_RETRIEVE_PRODUCTS = "Erro ao visualizar cadastro de produtos";
    public static final String ERROR_DELETE_PRODUCT = "Erro ao excluir produto";
    public static final String ERROR_TEMP_FILE = "Erro ao renomear o arquivo temporário";
    public static final String ERROR_UPDATE_PRODUCT = "Erro ao alterar cadastro de produto";
    public static final String SUCCESS_UPDATE = "Cadastro alterado com sucesso.\n";
}
