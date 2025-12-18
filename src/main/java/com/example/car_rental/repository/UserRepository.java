package com.example.car_rental.repository;

import com.example.car_rental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByDriverLicenseSeriesAndDriverLicenseNumber(String series, String number);
    boolean existsByPassportSeriesAndPassportNumber(String series, String number);
    long countByRole(String role);
}
