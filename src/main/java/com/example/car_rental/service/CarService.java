package com.example.car_rental.service;

import com.example.car_rental.model.Brand;
import com.example.car_rental.model.Car;
import org.springframework.stereotype.Service;
import com.example.car_rental.repository.CarRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления автомобилями в системе аренды.
 * <p>
 * Предоставляет бизнес-логику для работы с автомобилями, включая:
 * <ul>
 *     <li>Получение списка доступных для аренды автомобилей (статус AVAILABLE)</li>
 *     <li>CRUD операции над автомобилями</li>
 *     <li>Извлечение уникальных значений (марки, года, цвета) для фильтрации</li>
 *     <li>Подсчет автомобилей по марке и модели</li>
 * </ul>
 * <p>
 * Статусы автомобилей: AVAILABLE (свободен), RENTED (сдан в аренду),
 * RESERVED (зарезервирован), MAINTENANCE (на обслуживании).
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Service
public class CarService {

    /**
     * Репозиторий для работы с автомобилями
     */
    private final CarRepository carRepository;

    /**
     * Конструктор сервиса автомобилей.
     *
     * @param carRepository репозиторий автомобилей
     */
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    /**
     * Возвращает список всех автомобилей со статусом AVAILABLE.
     * Используется для отображения доступных для аренды автомобилей.
     *
     * @return список доступных автомобилей
     */
    public List<Car> getAvailableCars() {
        return carRepository.findAll().stream()
                .filter(car -> "AVAILABLE".equals(car.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех автомобилей независимо от статуса.
     *
     * @return список всех автомобилей
     */
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    /**
     * Находит автомобиль по ID.
     *
     * @param id ID автомобиля
     * @return объект автомобиля или null, если не найден
     */
    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    /**
     * Сохраняет автомобиль (создание или обновление).
     *
     * @param car объект автомобиля для сохранения
     * @return сохраненный автомобиль
     */
    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    /**
     * Удаляет автомобиль по ID.
     *
     * @param id ID автомобиля для удаления
     */
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    /**
     * Извлекает уникальные марки из списка автомобилей.
     * Используется для построения фильтров в интерфейсе.
     *
     * @param cars список автомобилей
     * @return множество уникальных марок
     */
    public Set<Brand> getBrandsFromCars(List<Car> cars) {
        return cars.stream()
                .map(Car::getBrand)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Извлекает уникальные года выпуска из списка автомобилей с сортировкой по возрастанию.
     * Используется для построения фильтров в интерфейсе.
     *
     * @param cars список автомобилей
     * @return отсортированный список уникальных годов выпуска
     */
    public List<Integer> getYearsFromCars(List<Car> cars) {
        return cars.stream()
                .map(Car::getYearOfManufacture)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Извлекает уникальные цвета из списка автомобилей.
     * Используется для построения фильтров в интерфейсе.
     *
     * @param cars список автомобилей
     * @return множество уникальных цветов (исключая пустые значения)
     */
    public Set<String> getColorsFromCars(List<Car> cars) {
        return cars.stream()
                .map(Car::getColor)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.toSet());
    }

    /**
     * Подсчитывает количество автомобилей с указанной моделью.
     * Используется для проверки возможности удаления модели.
     *
     * @param model объект модели автомобиля
     * @return количество автомобилей данной модели
     */
    public long countCarsByModel(com.example.car_rental.model.Model model) {
        return carRepository.countByModel(model);
    }

    /**
     * Подсчитывает количество автомобилей с указанной маркой.
     * Используется для проверки возможности удаления марки.
     *
     * @param brand объект марки автомобиля
     * @return количество автомобилей данной марки
     */
    public long countCarsByBrand(Brand brand) {
        return carRepository.countByBrand(brand);
    }
}
