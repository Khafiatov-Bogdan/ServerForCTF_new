package com.ctf.config;

import com.ctf.model.Challenge;
import com.ctf.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private ChallengeRepository challengeRepository;
    
    @Override
    public void run(String... args) throws Exception {
        createSqlInjectionChallenge();
        createXssChallenge();
        createAuthBypassChallenge();
        createPathTraversalChallenge();
        createCsrfChallenge();
    }
    
    private void createSqlInjectionChallenge() {
        if (challengeRepository.findByTitle("SQL Injection Basic").isEmpty()) {
            Challenge challenge = new Challenge(
                "SQL Injection Basic",
                "Обойдите аутентификацию с помощью SQL инъекции. Найдите флаг в базе данных.",
                "web",
                100,
                "easy",
                "flag{sql_injection_success_2024}",
                "Используйте ' OR '1'='1 в поле username",
                "Попробуйте использовать символы ' и -- для обхода аутентификации"
            );
            challengeRepository.save(challenge);
            System.out.println("SQL Injection challenge created");
        }
    }
    
    private void createXssChallenge() {
        if (challengeRepository.findByTitle("Reflected XSS").isEmpty()) {
            Challenge challenge = new Challenge(
                "Reflected XSS",
                "Внедрите XSS скрипт, который выполнится при загрузке страницы.",
                "web",
                150,
                "easy", 
                "flag{xss_successful_2024}",
                "Используйте <script>alert('XSS')</script>",
                "Попробуйте тег <script> для выполнения JavaScript"
            );
            challengeRepository.save(challenge);
        }
    }
    
    private void createAuthBypassChallenge() {
        if (challengeRepository.findByTitle("Authentication Bypass").isEmpty()) {
            Challenge challenge = new Challenge(
                "Authentication Bypass",
                "Найдите способ обойти аутентификацию и получить доступ к защищенной странице.",
                "web",
                120,
                "easy",
                "flag{auth_bypass_success_2024}",
                "Проверьте cookies или параметры URL",
                "Ищите скрытые параметры в URL или проверьте cookies"
            );
            challengeRepository.save(challenge);
        }
    }
    
    private void createPathTraversalChallenge() {
        if (challengeRepository.findByTitle("Path Traversal").isEmpty()) {
            Challenge challenge = new Challenge(
                "Path Traversal", 
                "Используйте уязвимость Path Traversal для чтения защищенных файлов.",
                "web",
                200,
                "medium",
                "flag{path_traversal_success_2024}",
                "Используйте ../../../etc/passwd",
                "Попробуйте использовать ../ для перемещения по директориям"
            );
            challengeRepository.save(challenge);
        }
    }
    
    private void createCsrfChallenge() {
        if (challengeRepository.findByTitle("CSRF Attack").isEmpty()) {
            Challenge challenge = new Challenge(
                "CSRF Attack",
                "Создайте CSRF атаку для изменения данных пользователя.", 
                "web",
                180,
                "medium",
                "flag{csrf_attack_success_2024}",
                "Создайте форму, которая автоматически отправляется",
                "Используйте автоматическую отправку формы"
            );
            challengeRepository.save(challenge);
        }
    }
}