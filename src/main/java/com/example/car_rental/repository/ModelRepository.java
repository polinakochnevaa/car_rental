package com.example.car_rental.repository;

import com.example.car_rental.model.Brand;
import com.example.car_rental.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModelRepository extends JpaRepository<Model, Long> {
    List<Model> findByBrandId(Long brandId);
    long countByBrand(Brand brand);
}
