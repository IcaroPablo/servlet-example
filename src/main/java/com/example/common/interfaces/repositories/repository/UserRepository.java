package com.example.common.interfaces.repositories.repository;

import com.example.common.interfaces.repositories.UserRepositoryView;
import com.example.common.interfaces.rest.dtos.UserDto;
import com.example.domain.entities.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.common.constants.Constants.BD_USERS;
import static com.example.common.constants.Constants.ERROR_CREATE_USER;
import static com.example.common.constants.Constants.ERROR_READ_FILE;
import static com.example.common.infrastructure.utils.Present.print;
import static com.example.common.interfaces.rest.dtos.UserDto.fromUser;

public class UserRepository implements UserRepositoryView {

    User user = new User();

    @Override
    public boolean login(String userName, String password, Boolean administrador) {
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_USERS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length == 3 && dados[0].equals(userName) && dados[1].equals(password)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            print(ERROR_READ_FILE);
        }
        return false;
    }

    @Override
    public UserDto saveDataAccessUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BD_USERS, true))) {
            writer.write(user.getCpf() + "," + user.getPassword() + "," + user.getIsAdministrador());
            writer.newLine();
            return fromUser(user);
        } catch (IOException e) {
            print(ERROR_CREATE_USER);
            return null;
        }
    }

    @Override
    public UserDto saveDataUser(String cpf, String userName, String phone, Boolean isAdministrador) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BD_USERS, true))) {
            writer.write(cpf + "," + userName + "," + phone + "," + isAdministrador);
            writer.newLine();
            user.setCpf(cpf);
            user.setUsername(userName);
            user.setPhone(phone);
            user.setIsAdministrador(isAdministrador);
            return fromUser(user);
        } catch (IOException e) {
            print(ERROR_CREATE_USER);
            return null;
        }
    }

    @Override
    public UserDto getUser(String cpf, Boolean isAdministrador) {
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_USERS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length == 4 &&
                        dados[0].equals(cpf) &&
                        Boolean.TRUE.equals(Boolean.parseBoolean(dados[3]) == isAdministrador)) {
                    user.setCpf(dados[0]);
                    user.setUsername(dados[1]);
                    user.setPhone(dados[2]);
                    user.setIsAdministrador(Boolean.parseBoolean(dados[3]));
                    return fromUser(user);
                }
            }
            return null;
        } catch (Exception e) {
            print("Usuário não encontrado");
        }
        return null;
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_USERS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length == 4) {
                    user.setCpf(dados[0]);
                    user.setUsername(dados[1]);
                    user.setPhone(dados[2]);
                    user.setIsAdministrador(Boolean.parseBoolean(dados[3]));
                    users.add(fromUser(user));
                }
            }
            return users;
        } catch (Exception e) {
            print("Não há usuários cadastrados");
        }
        return null;
    }

    @Override
    public boolean userExists(String cpf, Boolean isAdministrador) {
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_USERS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length == 4 &&
                        dados[0].equals(cpf) &&
                        Boolean.TRUE.equals(Boolean.parseBoolean(dados[3]) == isAdministrador)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            print("Não há um usuário cadastrado com o cpf informado");
        }
        return false;
    }

    @Override
    public boolean updateUser(UserDto userDto) {
        var updatedUser = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_USERS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length == 4 && dados[0].equals(userDto.getCpf())) {
                    dados[1] = userDto.getUsername();
                    dados[2] = userDto.getPhone();
                    dados[3] = String.valueOf(userDto.getIsAdministrador());
                    updatedUser = true;
                    return updatedUser;
                }
            }
            return updatedUser;
        } catch (Exception e) {
            print("Falha ao atualizar usuário");
        }
        return updatedUser;
    }

    @Override
    public boolean deleteUser(String cpf, Boolean isAdministrador) {
        File inputFile = new File(BD_USERS);
        File tempFile = new File(inputFile.getAbsolutePath() + ".tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            boolean deletedUser = false;

            while ((currentLine = reader.readLine()) != null) {
                String[] dados = currentLine.split(",");
                if (dados.length == 4 &&
                        dados[0].equals(cpf) &&
                        Boolean.TRUE.equals(Boolean.parseBoolean(dados[3]) == isAdministrador)) {
                    deletedUser = true;
                    continue;
                }
                writer.write(currentLine);
                writer.newLine();
            }

            if (!inputFile.delete()) {
                print("Could not delete original file");
                return false;
            }
            if (!tempFile.renameTo(inputFile)) {
                print("Could not rename temp file");
                return false;
            }

            return deletedUser;

        } catch (Exception e) {
            e.printStackTrace();
            print("Falha ao deletar usuário");
            return false;
        }
    }
}
