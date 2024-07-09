package com.example.common.infrastructure.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    static String roothPath = "/Users/joao/Documents/repos-jv/bd-lojinha/";

    private static boolean arquivoExiste(String nomeArquivo) {
        if (nomeArquivo == null || nomeArquivo.isEmpty()) {
            throw new IllegalArgumentException("O nome do arquivo não pode ser nulo ou vazio.");
        }
        return new File(nomeArquivo).exists();
    }

    public static void createIfNotExists(String fileName, String cabecalho) {
        if (!arquivoExiste(fileName)) {
            try {
                Files.createDirectories(Paths.get(roothPath));

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                    writer.write(cabecalho);
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
