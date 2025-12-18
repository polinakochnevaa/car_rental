package com.example.car_rental.repository;

import com.example.car_rental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByClient_EmailOrderByCreatedAtDesc(String email);
    List<Rental> findByStatus(String status);
}
