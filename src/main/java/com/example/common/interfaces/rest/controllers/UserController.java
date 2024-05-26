package com.example.common.interfaces.rest.controllers;

import com.example.common.interfaces.rest.dtos.UserDto;
import com.example.common.interfaces.service.UserService;
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
public class UserController extends HttpServlet {

	ObjectMapper objectMapper = new ObjectMapper();
	private static final Gson gson = new Gson();
	UserDto userDto = new UserDto();
	private UserService userService;


    @Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		userDto.setCpf(req.getParameter("cpf"));
		userDto.setUsername(req.getParameter("username"));
		userDto.setPhone(req.getParameter("telefone"));
		userDto.setPassword(req.getParameter("password"));
		userDto.setIsAdministrador(Boolean.parseBoolean(req.getParameter("administrador")));

		userService.saveDataAccessUser(UserDto.builder().cpf(userDto.getCpf()).password(userDto.getPassword()).isAdministrador(userDto.getIsAdministrador()).build());

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
		String pathInfo = req.getPathInfo();

		if (pathInfo != null) {
			if (pathInfo.equals("/user")) {
				String cpf = req.getParameter("cpf");
				Boolean isAdministrador = Boolean.parseBoolean(req.getParameter("administrador"));
				userDto = userService.getUser(cpf, isAdministrador);

				if (userDto != null) {
					String jsonUser = gson.toJson(userDto);
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType("application/json");
					resp.getWriter().write(jsonUser);
				} else {
					resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado");
				}
			}
			if (pathInfo.equals("/users")) {
				List<UserDto> users = userService.getAll();
				if (users != null && !users.isEmpty()) {
				String jsonResponse = objectMapper.writeValueAsString(users);
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setContentType("application/json");
				resp.getWriter().write(jsonResponse);
				} else {
					resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Não há usuários cadastrados");
				}
			} if (pathInfo.equals("/check-user")) {
				String cpf = req.getParameter("cpf");
				Boolean isAdministrador = Boolean.parseBoolean(req.getParameter("administrador"));
				if (userService.userExists(cpf, isAdministrador)) {
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.setContentType("application/json");
				} else {
					resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Não há um usuário cadastrado com o cpf informado");
				}
			} else {
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				resp.getWriter().write("Resource not found");
			}
		}
	}

	@Override
	public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		userDto.setCpf(req.getParameter("cpf"));
		userDto.setUsername(req.getParameter("novoNome"));
		userDto.setPhone(req.getParameter("novoTelefone"));
		userDto.setIsAdministrador(Boolean.parseBoolean(req.getParameter("administrador")));
		if(userService.updateUser(userDto)) {
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

		if(userService.deleteUser(cpf, isAdministrador)) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write("User deleted successfully");
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found");
		}
	}
}
