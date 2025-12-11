package com.example.demo.service;

import com.example.demo.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<Users> getTop3Users() {
        return usersRepository.findTop3ByOrderByPointsDesc();
    }

    public List<Users.UserNamePointsDTO> getAllNames() {
        return usersRepository.findAllNames();
    }

    public Users registerUser(String login, String rawPassword) {
        if (usersRepository.existsByLogin(login)) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        Users user = new Users();
        user.setLogin(login);
        user.setPassword(rawPassword); // Без BCrypt
        user.setPoints(0);
        user.setPointsLab(0);

        return usersRepository.save(user);
    }

    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Optional<Users> getUserByLogin(String login) {
        return usersRepository.findByLogin(login);
    }

    public boolean checkPassword(String login, String rawPassword) {
        Optional<Users> userOpt = usersRepository.findByLogin(login);
        return userOpt.map(user -> rawPassword.equals(user.getPassword()))
                .orElse(false);
    }

    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }

    /** Создать или обновить пользователя */
    public Users createOrUpdateUser(String login, String rawPassword, int points, int pointsLab) {
        Optional<Users> userOpt = usersRepository.findByLogin(login);
        Users user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            user = new Users();
            user.setLogin(login);
        }

        user.setPassword(rawPassword); // Без BCrypt
        user.setPoints(points);
        user.setPointsLab(pointsLab);

        return usersRepository.save(user);
    }


    /** Получаем кол-во обычных очков */
    public int getPoints(String login) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPoints();
    }

    /** Установить обычные очки */
    public void setPoints(String login, int newPoints) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPoints(newPoints);
        usersRepository.save(user);
    }

    /** Добавляем обычные очки */
    public void addPoints(String login, int amount) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPoints(user.getPoints() + amount);
        usersRepository.save(user);
    }

    /** Списываем обычные очки */
    public void subtractPoints(String login, int amount) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int newPoints = Math.max(0, user.getPoints() - amount);
        user.setPoints(newPoints);

        usersRepository.save(user);
    }

    /** Получаем лабораторные очки */
    public int getPointsLab(String login) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPointsLab();
    }

    /** Устанавливаем лабораторные очки */
    public void setPointsLab(String login, int newPoints) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPointsLab(newPoints);
        usersRepository.save(user);
    }

    /** Добавляем лабораторные очки */
    public void addPointsLab(String login, int amount) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPointsLab(user.getPointsLab() + amount);
        usersRepository.save(user);
    }

    /** Списываем лабораторные очки */
    public void subtractPointsLab(String login, int amount) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int newPoints = Math.max(0, user.getPointsLab() - amount);
        user.setPointsLab(newPoints);
        usersRepository.save(user);
    }

    // ====== METHODS FOR Base CONTROLLER (чтобы не ломать Base.java) ======

    public int getLabPoints(String login) {
        return getPointsLab(login);
    }

    public void setLabPoints(String login, int amount) {
        setPointsLab(login, amount);
    }

    public void addLabPoints(String login, int amount) {
        addPointsLab(login, amount);
    }

    public void subtractLabPoints(String login, int amount) {
        subtractPointsLab(login, amount);
    }
}
