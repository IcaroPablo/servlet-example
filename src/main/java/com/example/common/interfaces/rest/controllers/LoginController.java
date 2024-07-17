package com.example.common.interfaces.rest.controllers;

import com.example.common.interfaces.repositories.UserRepositoryView;
import com.example.common.interfaces.repositories.repository.ShoppingRepository;
import com.example.common.interfaces.repositories.repository.UserRepository;
import com.example.common.interfaces.rest.dtos.LoginDto;
import com.example.common.interfaces.rest.dtos.UserDto;
import com.example.common.interfaces.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public class LoginController extends HttpServlet {

	private static final Logger logger = Logger.getLogger(ShoppingRepository.class.getName());
	private final UserService userService;
	ObjectMapper objectMapper = new ObjectMapper();
	private final UserRepositoryView userRepositoryView;


	public LoginController() {
		this.userRepositoryView = new UserRepository();
		this.userService = new UserService(userRepositoryView);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LoginDto loginDto = objectMapper.readValue(req.getInputStream(), LoginDto.class);
		logger.log(Level.INFO, "Path info: {0}", new Object[]{req.getPathInfo()});
		logger.log(Level.INFO, "Tentativa de Login: {0}", new Object[]{loginDto.getCpf()});
		UserDto user = userService.login(loginDto);

		if (user != null) {
			resp.setStatus(HttpServletResponse.SC_OK);
			String jsonResponse = objectMapper.writeValueAsString(user);
			resp.setContentType("application/json");
			resp.getWriter().write(jsonResponse);
		} else {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
