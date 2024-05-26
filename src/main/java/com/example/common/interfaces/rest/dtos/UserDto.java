package com.example.common.interfaces.rest.dtos;

import com.example.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String cpf;
    private String username;
    private String phone;
    private String password;
    private Boolean isAdministrador;


    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setCpf(user.getCpf());
        userDto.setUsername(user.getUsername());
        userDto.setPhone(user.getPhone());
        userDto.setIsAdministrador(user.getIsAdministrador());
        return userDto;
    }
}
