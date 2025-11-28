package com.example.demo.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UsersService usersService;

    public AdminInitializer(UsersService usersService) {
        this.usersService = usersService;
    }

    @Override
    public void run(String... args) throws Exception {
        String adminLogin = "admin";
        String adminPassword = "K@gro_Alchemistry_Red_bo$$";
        int adminPoints = -999;
        usersService.createOrUpdateUser(adminLogin, adminPassword, adminPoints);
        System.out.println("Админ-пользователь установлен: " + adminLogin);
    }
}
