package com.example.car_rental.repository;

import com.example.car_rental.model.Brand;
import com.example.car_rental.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для работы с моделями автомобилей.
 * <p>
 * Предоставляет методы для управления моделями автомобилей,
 * включая поиск моделей по ID марки и подсчет количества моделей
 * для конкретной марки. Используется для каскадной загрузки моделей
 * при выборе марки автомобиля.
 *
 * @author ИжДрайв
 * @version 1.0
 */
public interface ModelRepository extends JpaRepository<Model, Long> {
    /**
     * Находит все модели автомобилей по ID марки.
     *
     * @param brandId ID марки автомобиля
     * @return список моделей указанной марки
     */
    List<Model> findByBrandId(Long brandId);

    /**
     * Подсчитывает количество моделей для указанной марки.
     *
     * @param brand объект марки
     * @return количество моделей данной марки
     */
    long countByBrand(Brand brand);
}
