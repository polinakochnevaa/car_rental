package com.example.car_rental.repository;

import com.example.car_rental.model.Brand;
import com.example.car_rental.model.Car;
import com.example.car_rental.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с автомобилями.
 * <p>
 * Предоставляет методы для управления автомобилями в системе аренды,
 * включая подсчет автомобилей по модели и марке. Используется для
 * проверки возможности удаления марок и моделей (нельзя удалить,
 * если есть привязанные автомобили).
 *
 * @author ИжДрайв
 * @version 1.0
 */
public interface CarRepository extends JpaRepository<Car, Long> {
    /**
     * Подсчитывает количество автомобилей указанной модели.
     *
     * @param model объект модели автомобиля
     * @return количество автомобилей данной модели
     */
    long countByModel(Model model);

    /**
     * Подсчитывает количество автомобилей указанной марки.
     *
     * @param brand объект марки автомобиля
     * @return количество автомобилей данной марки
     */
    long countByBrand(Brand brand);
}
