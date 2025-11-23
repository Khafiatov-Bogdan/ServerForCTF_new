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
    
    public boolean validateSqlInjection(String username, String password) {
    // Нормализуем пробелы
    String normalizedInput = (username + " " + password)
        .replaceAll("\\s+", " ")
        .toUpperCase()
        .trim();
    
    // Проверяем различные варианты с разными пробелами
    String[] patterns = {
        "' OR '1'='1", "' OR '1' = '1", "' OR '1' = '1",
        "' OR 1=1", "' OR 1 = 1", "ADMIN' --",
        "' OR 'A'='A", "' OR ''='"
    };
    
    for (String pattern : patterns) {
        String normalizedPattern = pattern.replaceAll("\\s+", " ");
        if (normalizedInput.contains(normalizedPattern.toUpperCase())) {
            System.out.println("SQL Injection detected: " + pattern);
            return true;
        }
    }
    
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

    public String getChallengeHint(String challengeName) {
    return getChallengeByTitle(challengeName)
        .map(Challenge::getHints)
        .orElse("Подсказка не найдена");
}
}