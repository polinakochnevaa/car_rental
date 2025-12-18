package com.example.car_rental.service;

import com.example.car_rental.model.Brand;
import com.example.car_rental.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления марками автомобилей.
 * <p>
 * Предоставляет бизнес-логику для работы с марками автомобилей,
 * включая операции получения, создания, обновления и удаления.
 * Используется контроллерами для взаимодействия с репозиторием марок.
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Service
public class BrandService {

    /**
     * Репозиторий для работы с марками автомобилей
     */
    private final BrandRepository brandRepository;

    /**
     * Конструктор сервиса марок автомобилей.
     *
     * @param brandRepository репозиторий марок
     */
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    /**
     * Возвращает список всех марок автомобилей.
     *
     * @return список всех марок
     */
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    /**
     * Находит марку автомобиля по ID.
     *
     * @param id ID марки
     * @return объект марки или null, если не найдена
     */
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id).orElse(null);
    }

    /**
     * Сохраняет марку автомобиля (создание или обновление).
     *
     * @param brand объект марки для сохранения
     * @return сохраненная марка
     */
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }

    /**
     * Удаляет марку автомобиля по ID.
     *
     * @param id ID марки для удаления
     */
    public void deleteBrand(Long id) {
        brandRepository.deleteById(id);
    }
}
