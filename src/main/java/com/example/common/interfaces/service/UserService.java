package com.example.common.interfaces.service;

import com.example.common.interfaces.repositories.UserRepositoryView;
import com.example.common.interfaces.rest.dtos.LoginDto;
import com.example.common.interfaces.rest.dtos.UserDto;
import com.example.domain.entities.User;

import java.util.List;

import static com.example.common.infrastructure.utils.Present.println;

public class UserService {

    private final UserRepositoryView userRepository;

    public UserService(UserRepositoryView userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto login(LoginDto loginDto) {
        return userRepository.login(loginDto.getCpf(), loginDto.getPassword(), loginDto.getAdministrador());
    }

    public UserDto saveDataUser(String cpf, String userName, String phone, Boolean isAdministrador) {
        return userRepository.saveDataUser(cpf, userName, phone, isAdministrador);
    }

    public UserDto saveDataAccessUser(UserDto userDto){
        User user = new User();
        user.setCpf(userDto.getCpf());
        user.setPassword(userDto.getPassword());
        user.setIsAdministrador(userDto.getIsAdministrador());

        return userRepository.saveDataAccessUser(user);
    }

    public UserDto getUser(String cpf, Boolean isAdministrador) {
        return userRepository.getUser(cpf, isAdministrador);
    }

    public List<UserDto> getAll() {
        return userRepository.getAll();
    }

    public boolean userExists(String cpf, Boolean isAdministrador) {
        return userRepository.userExists(cpf, isAdministrador);
    }

    public boolean updateUser(UserDto userDto) {
        return userRepository.updateUser(userDto);
    }

    public boolean deleteUser(String cpf, Boolean isAdministrador) {
        return userRepository.deleteUser(cpf, isAdministrador);
    }
}
