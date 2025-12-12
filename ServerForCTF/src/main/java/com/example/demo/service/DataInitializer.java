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
                {"Антюфеев", "k7A2pd", "0"},
                {"Апян", "Qf92ma", "75"},
                {"Белголова", "tP4x81", "107"},
                {"Беляев", "Lm83qd", "87"},
                {"Варлаков", "xE72fk", "62"},
                {"Галиева", "A9pr63", "115"},
                {"Горланова", "wK55av", "109"},
                {"Гудков", "Zp38lt", "59"},
                {"Дюкарев", "hT91cz", "62"},
                {"Ивко", "Fs72mb", "40"},
                {"Ишмаев", "gQ81nv", "0"},
                {"Кисс", "Rk49ts", "125"},
                {"Куликов", "pD62we", "30"},
                {"Логинов", "Tb84ms", "75"},
                {"Люнгрин", "cS47lk", "103"},
                {"Мякишев", "uP93za", "40"},
                {"Новинькова", "Sd62qp", "32"},
                {"Петрыкина", "Mn84zr", "116"},
                {"Резников", "Yx71pw", "100"},
                {"Решетов", "Qr55tz", "10"},
                {"Сайфуллин", "Nf63bd", "40"},
                {"Сливински", "zC85rm", "116"},
                {"Сторож", "Va72lp", "59"},
                {"Суфияров", "Kb19qe", "110"},
                {"Франтасов", "Xq88vn", "93"},
                {"Шарипов", "Hg27tc", "40"},
                {"Шепитько", "Lf92qx", "112"},
                {"Адякова", "wM64ap", "109"},
                {"Архипова", "Dp37lc", "0"},
                {"Борисов", "Rq58nb", "92"},
                {"Волков", "Ke61yt", "106"},
                {"Гоголин", "Jx74fd", "88"},
                {"Деменин", "Cs53he", "87"},
                {"Драшко", "Ug98mk", "102"},
                {"Каштанов", "Bt41qr", "95"},
                {"Кожанов", "Pv86ln", "107"},
                {"Колга", "Hs64tp", "115"},
                {"Косица", "Zd73qw", "95"},
                {"Кузнецов", "Lm91fr", "109"},
                {"Ловцов", "Fx82ak", "91"},
                {"Логачев", "Tg37mv", "0"},
                {"Мухлаев", "Rc75sn", "99"},
                {"Носов", "Ab19wt", "0"},
                {"Олейников", "Qx12df", "1"},
                {"Радзиевская", "Ew88tp", "118"},
                {"Рассказов", "Sf71mv", "112"},
                {"Рогалев", "Jh29qw", "0"},
                {"Рузанов", "Uc83tn", "117"},
                {"Салихова", "Kd54vp", "94"},
                {"Сафронов", "Pb81tr", "96"},
                {"СеровИ", "Yn47kl", "108"},
                {"СеровЮ", "Xc93tw", "102"},
                {"Трусов", "Da66qs", "88"},
                {"Усманов", "Mt42fn", "7"},
                {"Фомин", "Rw31ke", "0"},
                {"Хасан", "Lp85qm", "36"},
                {"Хитров", "Ss94an", "90"},
                {"Черняев", "Vg57zw", "91"},
                {"Шинкарева", "Nj74ce", "109"},
                {"Шишкина", "Gh69rt", "114"},
                {"Шумкаев", "Qf82mk", "109"}
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
                {"SPARKI5", "5"},//1
                {"SPARKI9", "9"},//1
                {"SPARKI16", "16"},//1
                {"SPARKI19", "19"},//1
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

