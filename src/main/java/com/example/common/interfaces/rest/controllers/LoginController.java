package com.example.common.interfaces.rest.controllers;

import com.example.common.interfaces.rest.dtos.LoginDto;
import com.example.common.interfaces.service.UserService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class LoginController extends HttpServlet {

	private UserService userService;

    @Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String cpf = req.getParameter("cpf");
		String password = req.getParameter("password");
		Boolean isAdministrador = Boolean.parseBoolean(req.getParameter("isAdministrador"));

		LoginDto loginDto = new LoginDto(cpf, password, isAdministrador);
		boolean loginSuccessful = userService.login(loginDto);

		if (loginSuccessful)
			resp.setStatus(HttpServletResponse.SC_OK);
		else
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}
