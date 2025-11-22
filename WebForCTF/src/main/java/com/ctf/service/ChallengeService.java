package com.ctf.service;

import com.ctf.model.Challenge;
import com.ctf.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {
    
    @Autowired
    private ChallengeRepository challengeRepository;
    
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }
    
    public List<Challenge> getChallengesByCategory(String category) {
        return challengeRepository.findByCategory(category);
    }
    
    public Optional<Challenge> getChallengeById(Long id) {
        return challengeRepository.findById(id);
    }
    
    public Optional<Challenge> getChallengeByTitle(String title) {
        return challengeRepository.findByTitle(title);
    }
    
    // Уязвимый метод для SQL инъекции задания
    public boolean validateSqlInjection(String username, String password) {
        // Эмуляция уязвимой логики аутентификации
        String[] sqlInjectionPatterns = {
            "' OR '1'='1", "' OR 1=1--", "' OR 'a'='a",
            "admin'--", "' OR ''='", "' OR '1'='1'--",
            "' UNION SELECT", "';", "\" OR \"1\"=\"1"
        };
        
        String userInput = (username + " " + password).toUpperCase();
        
        for (String pattern : sqlInjectionPatterns) {
            if (userInput.contains(pattern.toUpperCase())) {
                System.out.println("SQL Injection detected: " + pattern);
                return true;
            }
        }
        
        // Также разрешаем обычный вход для тестирования
        return "admin".equals(username) && "admin123".equals(password);
    }
    
    public boolean validateFlag(Long challengeId, String userFlag) {
        Optional<Challenge> challenge = challengeRepository.findById(challengeId);
        return challenge.isPresent() && challenge.get().getFlag().equals(userFlag);
    }
    
    public boolean validateFlagByChallengeName(String challengeName, String userFlag) {
        Optional<Challenge> challenge = challengeRepository.findByTitle(challengeName);
        return challenge.isPresent() && challenge.get().getFlag().equals(userFlag);
    }
    
    public Challenge saveChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }
}