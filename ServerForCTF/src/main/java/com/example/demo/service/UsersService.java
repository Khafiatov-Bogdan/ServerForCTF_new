package com.example.demo.service;

import com.example.demo.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsersService(UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
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
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPoints(0); // ОБЯЗАТЕЛЬНО

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
        return userOpt.map(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .orElse(false);
    }

    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }

    /** Создать или обновить пользователя */
    public Users createOrUpdateUser(String login, String rawPassword, int points) {
        Optional<Users> userOpt = usersRepository.findByLogin(login);
        Users user;

        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            user = new Users();
            user.setLogin(login);
        }


        user.setPassword(passwordEncoder.encode(rawPassword));

        user.setPoints(points);

        return usersRepository.save(user);
    }




    /** Получаем кол-во очков */
    public int getPoints(String login) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPoints();
    }

    /** Рейтинг  */
    public void setPoints(String login, int newPoints) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPoints(newPoints);
        usersRepository.save(user);
    }

    /** Добавляем очки */
    public void addPoints(String login, int amount) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPoints(user.getPoints() + amount);
        usersRepository.save(user);
    }

    /** Списываем очки */
    public void subtractPoints(String login, int amount) {
        Users user = usersRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int newPoints = Math.max(0, user.getPoints() - amount);
        user.setPoints(newPoints);

        usersRepository.save(user);
    }

}
