package com.example.demo.service;

import com.example.demo.Promo;
import com.example.demo.repository.PromoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PromoService {

    private final PromoRepository promoRepository;

    @Autowired
    public PromoService(PromoRepository promoRepository) {
        this.promoRepository = promoRepository;
    }

    /**
     * Использовать промокод
     * @param code имя промокода
     * @return Optional с объектом Promo, если промокод действителен и не использован
     */
    public Optional<Promo> usePromoCode(String code) {
        Optional<Promo> promoOpt = promoRepository.findByName(code);

        if (promoOpt.isEmpty() || promoOpt.get().getIsUsed()) {
            return Optional.empty(); // промокод не найден или уже использован
        }

        Promo promo = promoOpt.get();
        promo.setIsUsed(true); // помечаем как использованный
        promoRepository.save(promo);

        return Optional.of(promo);
    }

    /**
     * Создать новый промокод с указанием количества баллов
     * @param code имя промокода
     * @param points количество баллов
     * @return созданный объект Promo или null, если промокод уже существует
     */
    public Promo createPromo(String code, int points) {
        if (promoRepository.findByName(code).isPresent()) {
            return null; // промокод уже существует
        }

        Promo promo = new Promo();
        promo.setName(code);
        promo.setPoints(points);
        promo.setIsUsed(false);

        return promoRepository.save(promo);
    }

    /**
     * Получить промокод по имени
     */
    public Optional<Promo> getPromoByName(String code) {
        return promoRepository.findByName(code);
    }
}
