package com.example.car_rental.service;

import com.example.car_rental.model.Car;
import com.example.car_rental.model.Rental;
import com.example.car_rental.model.User;
import com.example.car_rental.repository.RentalRepository;
import com.example.car_rental.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final CarService carService;

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository, CarService carService) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.carService = carService;
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getRentalsByClientEmail(String email) {
        return rentalRepository.findByClient_EmailOrderByCreatedAtDesc(email);
    }

    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    @Transactional
    public Rental createRental(Rental rental, String username) {
        User client = userRepository.findByEmail(username);
        if (client == null) {
            throw new IllegalArgumentException("Пользователь не найден: " + username);
        }
        rental.setClient(client);
        rental.setStatus("PENDING_PAYMENT");
        rental.setCreatedAt(LocalDateTime.now());

        // Установить статус машины в RESERVED
        Car car = rental.getCar();
        car.setStatus("RESERVED");
        carService.saveCar(car);

        // Сохраняем аренду
        return rentalRepository.save(rental);
    }

    @Transactional
    public void confirmPayment(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow();
        rental.setStatus("PAID");

        Car car = rental.getCar();
        car.setStatus("RENTED");
        carService.saveCar(car);

        rentalRepository.save(rental);
    }

    @Transactional
    public void cancelRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow();

        // Возвращаем статус машины в AVAILABLE
        Car car = rental.getCar();
        car.setStatus("AVAILABLE");
        carService.saveCar(car);

        // Обрабатываем аренду в зависимости от статуса
        if ("PENDING_PAYMENT".equals(rental.getStatus())) {
            // Для неоплаченных аренд - полностью удаляем из БД
            rentalRepository.delete(rental);
        } else if ("PAID".equals(rental.getStatus())) {
            // Для оплаченных аренд - помечаем как отмененные
            rental.setStatus("CANCELLED");
            rentalRepository.save(rental);
        }
    }

    // Проверка неоплаченных аренду старше 5 минут, сброс статусов
    @Scheduled(fixedDelay = 60000) // каждую минуту
    @Transactional
    public void checkExpiredPendingRentals() {
        LocalDateTime now = LocalDateTime.now();
        List<Rental> pendingRentals = rentalRepository.findByStatus("PENDING_PAYMENT");
        for (Rental rental : pendingRentals) {
            if (rental.getCreatedAt().plusMinutes(5).isBefore(now)) {
                cancelRental(rental.getId());
            }
        }
    }
}
