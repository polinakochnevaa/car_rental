package com.example.car_rental.repository;

import com.example.car_rental.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с марками автомобилей.
 * <p>
 * Предоставляет стандартные CRUD операции для управления марками автомобилей
 * через Spring Data JPA. Наследует базовый функционал от JpaRepository,
 * включая методы сохранения, удаления, поиска и получения всех марок.
 *
 * @author ИжДрайв
 * @version 1.0
 */
public interface BrandRepository extends JpaRepository<Brand, Long> {
}
