package com.example.car_rental.service;

import com.example.car_rental.model.Brand;
import com.example.car_rental.model.Car;
import org.springframework.stereotype.Service;
import com.example.car_rental.repository.CarRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    // Возвращает все автомобили со статусом AVAILABLE
    public List<Car> getAvailableCars() {
        return carRepository.findAll().stream()
                .filter(car -> "AVAILABLE".equals(car.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    // Возвращает уникальные бренды автомобилей из списка
    public Set<Brand> getBrandsFromCars(List<Car> cars) {
        return cars.stream()
                .map(Car::getBrand)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    // Возвращает уникальные года выпуска автомобилей из списка, отсортированные по возрастанию
    public List<Integer> getYearsFromCars(List<Car> cars) {
        return cars.stream()
                .map(Car::getYearOfManufacture)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Возвращает уникальные цвета автомобилей из списка
    public Set<String> getColorsFromCars(List<Car> cars) {
        return cars.stream()
                .map(Car::getColor)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.toSet());
    }

    // Подсчитывает количество автомобилей с данной моделью
    public long countCarsByModel(com.example.car_rental.model.Model model) {
        return carRepository.countByModel(model);
    }

    // Подсчитывает количество автомобилей с данной маркой
    public long countCarsByBrand(Brand brand) {
        return carRepository.countByBrand(brand);
    }
}
