package com.example.demo.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.stream.Collectors;

@Service
public class ExecService {

    /**
     * Запускает бинарный файл и передаёт входные данные через STDIN
     */
    public String runBinaryWithStdin(String command, String inputData) throws Exception {

        ProcessBuilder pb = new ProcessBuilder();
        pb.command(command);
        pb.redirectErrorStream(true); // объединяем stderr и stdout

        Process process = pb.start();

        // --- Отправляем данные в STDIN внешнего процесса ---
        try (OutputStream os = process.getOutputStream()) {
            os.write(inputData.getBytes());
            os.flush();
        }

        // --- Читаем вывод процесса ---
        String output;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            output = reader.lines().collect(Collectors.joining("\n"));
        }

        int exitCode = process.waitFor();

        return "Exit code: " + exitCode + "\n\n" + output;
    }

    // public String runBinaryWithStdin(String command, String inputData) throws Exception {
    //     String output = runBinaryWithStdin(command, inputData, "");
    //     return output;
    // }
}