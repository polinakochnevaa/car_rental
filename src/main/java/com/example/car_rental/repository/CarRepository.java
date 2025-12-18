package com.example.car_rental.repository;

import com.example.car_rental.model.Brand;
import com.example.car_rental.model.Car;
import com.example.car_rental.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
    long countByModel(Model model);
    long countByBrand(Brand brand);
}
