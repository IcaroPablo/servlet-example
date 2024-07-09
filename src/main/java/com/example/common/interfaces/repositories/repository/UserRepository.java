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
import static com.example.common.constants.Constants.BD_USERS_INFORMATION;
import static com.example.common.constants.Constants.ERROR_CREATE_USER;
import static com.example.common.constants.Constants.ERROR_READ_FILE;
import static com.example.common.infrastructure.utils.Present.print;
import static com.example.common.interfaces.rest.dtos.UserDto.fromUser;

public class UserRepository implements UserRepositoryView {

    User user = new User();

    private List<String[]> readFile(String filePath) throws IOException {
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.split(","));
            }
        }
        return lines;
    }

    private UserDto findUserInInformation(String cpf, Boolean administrador) throws IOException {
        List<String[]> infoLines = readFile(BD_USERS_INFORMATION);
        for (String[] data : infoLines) {
            if (data.length == 4 && data[0].equals(cpf) && Boolean.parseBoolean(data[3]) == administrador) {
                return new UserDto(data[0], data[1], data[2], null, administrador);
            }
        }
        return null;
    }


    @Override
    public UserDto login(String cpf, String password, Boolean administrador) {
        try {
            List<String[]> userLines = readFile(BD_USERS);
            for (String[] dados : userLines) {
                if (dados.length == 3 && dados[0].equals(cpf) && dados[1].equals(password) && Boolean.TRUE.equals(Boolean.parseBoolean(dados[2]) == administrador)) {
                    return findUserInInformation(cpf, administrador);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(ERROR_READ_FILE, e);
        }
        return null;
    }


    @Override
    public UserDto saveDataAccessUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BD_USERS, true))) {
            writer.write(user.getCpf() + "," + user.getPassword() + "," + user.getIsAdministrador().toString());
            writer.newLine();
            return fromUser(user);
        } catch (IOException e) {
            print(ERROR_CREATE_USER);
            return null;
        }
    }

    @Override
    public UserDto saveDataUser(String cpf, String userName, String phone, Boolean isAdministrador) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BD_USERS_INFORMATION, true))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_USERS_INFORMATION))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length == 4 &&
                        dados[0].equals(cpf) &&
                        Boolean.TRUE.equals(Boolean.parseBoolean(dados[3]) == isAdministrador)) {
                    UserDto userDto = new UserDto();
                    userDto.setCpf(dados[0]);
                    userDto.setUsername(dados[1]);
                    userDto.setPhone(dados[2]);
                    userDto.setIsAdministrador(Boolean.parseBoolean(dados[3]));
                    return userDto;
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
        try (BufferedReader reader = new BufferedReader(new FileReader(BD_USERS_INFORMATION))) {
            String linha;
            boolean primeiraLinha = true;
            while ((linha = reader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    continue;
                }
                String[] dados = linha.split(",");
                if (dados.length == 4) {
                    UserDto userDto = new UserDto();
                    userDto.setCpf(dados[0]);
                    userDto.setUsername(dados[1]);
                    userDto.setPhone(dados[2]);
                    userDto.setIsAdministrador(Boolean.parseBoolean(dados[3]));
                    users.add(userDto);
                } else {
                    System.out.println("linha mal-formada: " + linha);
                }
            }
            System.out.println("usuários lidos: " + users.size());
            return users;
        } catch (Exception e) {
            print("Não há usuários cadastrados");
        }
        return null;
    }

    @Override
    public boolean userExists(String cpf, Boolean isAdministrador) {

        try (BufferedReader reader = new BufferedReader(new FileReader(BD_USERS_INFORMATION))) {
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
        boolean updatedUser = false;
        List<String> novosDados = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(BD_USERS_INFORMATION))) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length == 4 && dados[0].equals(userDto.getCpf()) && Boolean.TRUE.equals(Boolean.parseBoolean(dados[3]) == userDto.getIsAdministrador())) {
                    dados[1] = userDto.getUsername() != null ? userDto.getUsername() : dados[1];
                    dados[2] = userDto.getPhone() != null ? userDto.getPhone() : dados[2];
                    novosDados.add(String.join(",", dados));
                    updatedUser = true;
                } else {
                    novosDados.add(linha);
                }
            }
        } catch (IOException e) {
            System.out.println("Falha ao ler o arquivo: " + e.getMessage());
            return false;
        }

        if (updatedUser) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(BD_USERS_INFORMATION))) {
                for (String novaLinha : novosDados) {
                    writer.write(novaLinha);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Falha ao escrever no arquivo: " + e.getMessage());
                return false;
            }
        }

        return updatedUser;
    }



    @Override
    public boolean deleteUser(String cpf, Boolean isAdministrador) {
        File usersFile = new File(BD_USERS);
        File usersTempFile = new File(usersFile.getAbsolutePath() + ".tmp");
        File usersInfoFile = new File(BD_USERS_INFORMATION);
        File usersInfoTempFile = new File(usersInfoFile.getAbsolutePath() + ".tmp");

        boolean deletedFromUsers = false;
        boolean deletedFromUsersInformation = false;

        try {
            deletedFromUsers = deleteUserFromFile(cpf, isAdministrador, usersFile, usersTempFile, 3);
            System.out.println("deletou do primeiro BD_USERS");
            deletedFromUsersInformation = deleteUserFromFile(cpf, isAdministrador, usersInfoFile, usersInfoTempFile, 4);
            System.out.println("deletou do segundo BD_USERS_INFORMATION");

            if (deletedFromUsers && deletedFromUsersInformation) {
                if (!usersTempFile.renameTo(usersFile)) {
                    throw new IOException("Could not rename temp file for users");
                }
                if (!usersInfoTempFile.renameTo(usersInfoFile)) {
                    throw new IOException("Could not rename temp file for users information");
                }
                return true;
            } else {
                usersTempFile.delete();
                usersInfoTempFile.delete();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            usersTempFile.delete();
            usersInfoTempFile.delete();
            System.out.println("Falha ao deletar usuário");
            return false;
        }
    }

    private boolean deleteUserFromFile(String cpf, Boolean isAdministrador, File inputFile, File tempFile, int expectedColumns) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            boolean deletedUser = false;

            while ((currentLine = reader.readLine()) != null) {
                String[] dados = currentLine.split(",");
                if (dados.length == expectedColumns &&
                        dados[0].equals(cpf) &&
                        (expectedColumns == 3 || Boolean.TRUE.equals(Boolean.parseBoolean(dados[3]) == isAdministrador))) {
                    deletedUser = true;
                    continue;
                }
                writer.write(currentLine);
                writer.newLine();
            }

            return deletedUser;
        }
    }

}
