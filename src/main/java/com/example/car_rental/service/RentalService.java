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

/**
 * Сервис для управления арендами автомобилей.
 * <p>
 * Предоставляет бизнес-логику для работы с арендами, включая:
 * <ul>
 *     <li>Создание новой аренды с резервированием автомобиля (статус PENDING_PAYMENT)</li>
 *     <li>Подтверждение оплаты (перевод аренды в статус PAID, автомобиля - в RENTED)</li>
 *     <li>Отмену аренды (возврат автомобиля в статус AVAILABLE)</li>
 *     <li>Автоматическую отмену неоплаченных аренд через 5 минут</li>
 *     <li>Получение списка аренд клиента и всех аренд для администратора</li>
 * </ul>
 * <p>
 * Статусы аренды: PENDING_PAYMENT (ожидает оплаты), PAID (оплачена), CANCELLED (отменена).
 * <p>
 * Фоновая задача (@Scheduled) каждую минуту проверяет неоплаченные аренды
 * и автоматически отменяет те, что старше 5 минут, освобождая зарезервированные автомобили.
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Service
public class RentalService {

    /**
     * Репозиторий для работы с арендами
     */
    private final RentalRepository rentalRepository;

    /**
     * Репозиторий для работы с пользователями
     */
    private final UserRepository userRepository;

    /**
     * Сервис для работы с автомобилями
     */
    private final CarService carService;

    /**
     * Конструктор сервиса аренды.
     *
     * @param rentalRepository репозиторий аренд
     * @param userRepository репозиторий пользователей
     * @param carService сервис автомобилей
     */
    public RentalService(RentalRepository rentalRepository, UserRepository userRepository, CarService carService) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.carService = carService;
    }

    /**
     * Возвращает список всех аренд в системе.
     *
     * @return список всех аренд
     */
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    /**
     * Возвращает список аренд клиента по email с сортировкой по дате создания (от новых к старым).
     *
     * @param email email клиента
     * @return список аренд клиента
     */
    public List<Rental> getRentalsByClientEmail(String email) {
        return rentalRepository.findByClient_EmailOrderByCreatedAtDesc(email);
    }

    /**
     * Находит аренду по ID.
     *
     * @param id ID аренды
     * @return объект аренды или null, если не найдена
     */
    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id).orElse(null);
    }

    /**
     * Создает новую аренду для указанного пользователя.
     * Устанавливает статус аренды PENDING_PAYMENT и резервирует автомобиль (статус RESERVED).
     *
     * @param rental объект аренды для создания
     * @param username email пользователя (используется как username)
     * @return созданная аренда
     * @throws IllegalArgumentException если пользователь не найден
     */
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

    /**
     * Подтверждает оплату аренды.
     * Переводит статус аренды в PAID и статус автомобиля в RENTED.
     *
     * @param rentalId ID аренды для подтверждения оплаты
     */
    @Transactional
    public void confirmPayment(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow();
        rental.setStatus("PAID");

        Car car = rental.getCar();
        car.setStatus("RENTED");
        carService.saveCar(car);

        rentalRepository.save(rental);
    }

    /**
     * Отменяет аренду и освобождает автомобиль.
     * Для неоплаченных аренд (PENDING_PAYMENT) - полностью удаляет запись из БД.
     * Для оплаченных аренд (PAID) - устанавливает статус CANCELLED.
     * В обоих случаях возвращает автомобиль в статус AVAILABLE.
     *
     * @param rentalId ID аренды для отмены
     */
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

    /**
     * Автоматически проверяет и отменяет неоплаченные аренды старше 5 минут.
     * Запускается каждую минуту как фоновая задача (@Scheduled).
     * Для каждой просроченной аренды освобождает зарезервированный автомобиль.
     */
    @Scheduled(fixedDelay = 60000)
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
