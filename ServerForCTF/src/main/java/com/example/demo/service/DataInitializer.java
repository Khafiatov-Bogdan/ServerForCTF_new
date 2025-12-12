package com.example.demo.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import com.example.demo.service.UsersService;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import com.example.demo.service.PromoService;
import com.example.demo.Task;
import com.example.demo.service.TaskService;
import com.example.demo.TaskPWN;
import com.example.demo.service.TaskPWNService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.io.*;
import java.math.BigInteger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class DataInitializer implements CommandLineRunner  {

    private final UsersService usersService;
    private final PromoService promoService;
    private final TaskService taskService;
    private final TaskPWNService taskPWNService;

    public DataInitializer(UsersService usersService, PromoService promoService, TaskService taskService, TaskPWNService taskPWNService) {
        this.usersService = usersService;
        this.promoService = promoService;
        this.taskService = taskService;
        this.taskPWNService = taskPWNService;
    }

     public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Переводим байты в hex строку
            StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // добавляем ведущий 0
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 не поддерживается", e);
        }
    }

    @Override
    @Transactional
    public void run(String... args) throws StreamReadException, DatabindException, IOException {

        // --- Пользователи ---
        String[][] defaultUsers = {
                {"Антюфеев", "123456", "0"},
                {"Апян", "123456", "75"},
                {"Белголова", "123456", "107"},
                {"Беляев", "123456", "87"},
                {"Варлаков", "123456", "62"},
                {"Галиева", "123456", "115"},
                {"Горланова", "123456", "109"},
                {"Гудков", "123456", "59"},
                {"Дюкарев", "123456", "62"},
                {"Ивко", "123456", "40"},
                {"Ишмаев", "123456", "0"},
                {"Кисс", "123456", "125"},
                {"Куликов", "123456", "30"},
                {"Логинов", "123456", "75"},
                {"Люнгрин", "123456", "103"},
                {"Мякишев", "123456", "40"},
                {"Новинькова", "123456", "32"},
                {"Петрыкина", "123456", "116"},
                {"Резников", "123456", "100"},
                {"Решетов", "123456", "10"},
                {"Сайфуллин", "123456", "40"},
                {"Сливински", "123456", "116"},
                {"Сторож", "123456", "59"},
                {"Суфияров", "123456", "110"},
                {"Франтасов", "123456", "93"},
                {"Шарипов", "123456", "40"},
                {"Шепитько", "123456", "112"},
                {"Адякова", "123456", "109"},
                {"Архипова", "123456", "0"},
                {"Борисов", "123456", "92"},
                {"Волков", "123456", "106"},
                {"Гоголин", "123456", "88"},
                {"Деменин", "123456", "87"},
                {"Драшко", "123456", "102"},
                {"Каштанов", "123456", "95"},
                {"Кожанов", "123456", "107"},
                {"Колга", "123456", "115"},
                {"Косица", "123456", "95"},
                {"Кузнецов", "123456", "109"},
                {"Ловцов", "123456", "91"},
                {"Логачев", "123456", "0"},
                {"Мухлаев", "123456", "99"},
                {"Носов", "123456", "0"},
                {"Олейников", "123456", "1"},
                {"Радзиевская", "123456", "118"},
                {"Рассказов", "123456", "112"},
                {"Рогалев", "123456", "0"},
                {"Рузанов", "123456", "117"},
                {"Салихова", "123456", "94"},
                {"Сафронов", "123456", "96"},
                {"СеровИ", "123456", "108"},
                {"СеровЮ", "123456", "102"},
                {"Трусов", "123456", "88"},
                {"Усманов", "123456", "7"},
                {"Фомин", "123456", "0"},
                {"Хасан", "123456", "36"},
                {"Хитров", "123456", "90"},
                {"Черняев", "123456", "91"},
                {"Шинкарева", "123456", "109"},
                {"Шишкина", "123456", "114"},
                {"Шумкаев", "123456", "109"}
        };


        for (String[] data : defaultUsers) {
            String login = data[0];
            String password = data[1];
            int lecturePoints = Integer.parseInt(data[2]);  // ← вот тут берем баллы

            if (usersService.getUserByLogin(login).isEmpty()) {
                usersService.createOrUpdateUser(login, password, 0, lecturePoints);
                System.out.println("Добавлен новый пользователь: " + login + " с баллами: " + lecturePoints);
            } else {
                System.out.println("Пользователь уже существует, пропускаем: " + login);
            }
        }


        // --- Промокоды ---
        String[][] defaultPromos = {
                {"CRINGE", "-5"},//1
                {"MINUS200", "20"},//1
                {"СОЛНЦЕ", "20"},//1
                {"OTVET", "10"},//1
                {"PARAMEFOZ", "21"},//на всякий
                {"KARLAPINGUS", "17"},//1
                {"VNIMATELNOST", "5"},//1
                {"FREE10", "7"},//1
                {"TIFON", "7"},//1
                {"MEMNOS", "12"},//1
                {"ANIGILATION", "8"},//1
                {"VOTTAKVOT", "1"},//1
                {"KRAB", "4"},//1
                {"ISHAK", "2"},//1
                {"ONEPEACEONELOVE", "22"},//1
                {"ZA_IMPERATORA!", "13"},//1
                {"GERMENTIT", "10"},//1
                {"SAMARA", "5"},//1
                {"UMBRA5", "5"},//1
                {"UMBRA10", "11"},//1
                {"UMBRA15", "14"},//1
                {"UMBRA20", "21"},//1
                {"POMOGITE5", "5"},//1
                {"POMOGITE10", "9"},//1
                {"POMOGITE15", "16"},//1
                {"POMOGITE20", "19"},//1
                {"OPANA", "5"}//1

        };

        for (String[] data : defaultPromos) {
            String code = data[0];
            int points = Integer.parseInt(data[1]);

            if (promoService.getPromoByName(code).isEmpty()) {
                promoService.createPromo(code, points);
                System.out.println("Добавлен новый промокод: " + code + " (" + points + " баллов)");
            } else {
                System.out.println("Промокод уже существует, пропускаем: " + code);
            }
        }

        // --- Задания ---
        String[][] defaultTasks = {
                {"Задание 1", "100"},
                {"Задание 2", "150"},
                {"Задание 3", "200"},
                {"Задание 4", "250"},
                {"Задание 5", "300"}
        };

        for (String[] data : defaultTasks) {
            String title = data[0];
            int points = Integer.parseInt(data[1]);

            if (taskService.getTaskByTitle(title).isEmpty()) {
                Task task = new Task();
                task.setTitle(title);
                task.setPoints(points);
                task.setSolved(false);
                taskService.createTask(task); // Создание через TaskService
                System.out.println("Добавлено новое задание: " + title + " (" + points + " баллов)");
            } else {
                System.out.println("Задание уже существует, пропускаем: " + title);
            }
        }


        ObjectMapper mapper = new ObjectMapper();

        ClassPathResource resource = new ClassPathResource("pwnTasks.json");

        List<TaskPWN> tasks = mapper.readValue(
                resource.getInputStream(),
                new TypeReference<List<TaskPWN>>() {}
        );

        for (TaskPWN task : tasks) {
                task.setFlag(sha256(task.getFlag()));
            if (taskPWNService.getTaskByTitle(task.getTitle()).isEmpty()) {
                taskPWNService.createTask(task);
                System.out.println("Добавлено новое задание: " + task.getTitle() + " (" + task.getPoints() + " баллов)");
            } else {
                System.out.println("Задание уже существует, пропускаем: " + task.getTitle());
            }
        }

        System.out.println("Импортировано " + tasks.size() + " задач");

    }
}

