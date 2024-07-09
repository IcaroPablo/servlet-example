package com.example.common.interfaces.rest.controllers;

import com.example.common.infrastructure.utils.FileUtils;
import com.example.common.interfaces.repositories.UserRepositoryView;
import com.example.common.interfaces.repositories.repository.UserRepository;
import com.example.common.interfaces.rest.dtos.UserDto;
import com.example.common.interfaces.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.example.common.constants.Constants.BD_USERS;
import static com.example.common.constants.Constants.BD_USERS_CABECALHO;
import static com.example.common.constants.Constants.BD_USERS_INFORMATION;
import static com.example.common.constants.Constants.BD_USERS_INFORMATION_CABECALHO;

public class UserController extends HttpServlet {

	ObjectMapper objectMapper = new ObjectMapper();
	private static final Gson gson = new Gson();
	private final UserRepositoryView userRepositoryView;
	private final UserService userService;

	public UserController() {
		this.userRepositoryView = new UserRepository();
		this.userService = new UserService(userRepositoryView);
		FileUtils.createIfNotExists(BD_USERS_INFORMATION, BD_USERS_INFORMATION_CABECALHO);
		FileUtils.createIfNotExists(BD_USERS, BD_USERS_CABECALHO);
	}

    @Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo != null && pathInfo.equals("/update")) {
			userUpdate(req, resp);
			return;
		}

		UserDto userDto = objectMapper.readValue(req.getInputStream(), UserDto.class);
		userService.saveDataAccessUser(userDto);

		var savedUserDto = userService.saveDataUser(userDto.getCpf(), userDto.getUsername(), userDto.getPhone(), userDto.getIsAdministrador());

		if (savedUserDto == null) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("Error saving user data");
			return;
		}
		resp.setStatus(HttpServletResponse.SC_CREATED);
		String jsonResponse = objectMapper.writeValueAsString(savedUserDto);

		resp.setContentType("application/json");
		resp.getWriter().write(jsonResponse);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		System.out.println("doGet method called");

		String pathInfo = req.getPathInfo();
		System.out.println("Path info: " + pathInfo);

		if (pathInfo != null) {
			if (pathInfo.equals("/user")) {
				System.out.println("Fetching user by CPF");
				String cpf = req.getParameter("cpf");
				Boolean isAdministrador = Boolean.parseBoolean(req.getParameter("administrador"));
				UserDto userDto = userService.getUser(cpf, isAdministrador);

				if (userDto != null) {
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType("application/json");
					resp.getWriter().write(objectMapper.writeValueAsString(userDto));
				} else {
					resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado");
				}
			} else if (pathInfo.equals("/users")) {
				System.out.println("Fetching all users");
				List<UserDto> users = userService.getAll();
				if (users != null && !users.isEmpty()) {
					String jsonResponse = objectMapper.writeValueAsString(users);
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType("application/json");
					resp.getWriter().write(jsonResponse);
				} else {
					resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
					resp.getWriter().write("Não há usuários cadastrados");
				}
			} else if (pathInfo.equals("/check-user")) {
				System.out.println("Checking user existence");
				String cpf = req.getParameter("cpf");
				Boolean isAdministrador = Boolean.parseBoolean(req.getParameter("administrador"));
				if (userService.userExists(cpf, isAdministrador)) {
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType("application/json");
				} else {
					resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Não há um usuário cadastrado com o cpf informado");
				}
			} else {
				System.out.println("Invalid path");
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				resp.getWriter().write("Resource not found");
			}
		} else {
			System.out.println("Path info is null");
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("Invalid request");
		}
	}


	private void userUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		System.out.println("chamou o patch");
		UserDto userDto = objectMapper.readValue(req.getInputStream(), UserDto.class);

		if ("null".equals(userDto.getCpf())) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "É preciso enviar os dados corretos.");
		}

		if (userService.updateUser(userDto)) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write("User updated successfully");
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found");
		}
	}


	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String cpf = req.getParameter("cpf");
		Boolean isAdministrador = Boolean.parseBoolean(req.getParameter("administrador"));

		if (userService.deleteUser(cpf, isAdministrador)) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write("User deleted successfully");
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found");
		}
	}

}
