package com.example.common.interfaces.repositories;

import com.example.common.interfaces.rest.dtos.UserDto;
import com.example.domain.entities.User;

import java.util.List;

public interface UserRepositoryView {

    boolean login(String userName, String password, Boolean administrador);

    UserDto saveDataAccessUser(User user);

    UserDto saveDataUser(String cpf, String userName, String phone, Boolean isAdministrador);

    UserDto getUser(String cpf, Boolean isAdministrador);

    List<UserDto> getAll();

    boolean userExists(String cpf, Boolean isAdministrador);

    boolean updateUser(UserDto userDto);

    boolean deleteUser(String cpf, Boolean isAdministrador);
}
