package com.example.car_rental.repository;

import com.example.car_rental.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для работы с арендами автомобилей.
 * <p>
 * Предоставляет методы для управления арендами, включая поиск аренд
 * конкретного клиента (по email) с сортировкой по дате создания
 * и поиск аренд по статусу. Используется для отображения списка аренд
 * пользователя и автоматической отмены неоплаченных заказов.
 *
 * @author ИжДрайв
 * @version 1.0
 */
public interface RentalRepository extends JpaRepository<Rental, Long> {
    /**
     * Находит все аренды клиента по его email с сортировкой по дате создания (от новых к старым).
     *
     * @param email email клиента
     * @return список аренд клиента, отсортированный по дате создания (DESC)
     */
    List<Rental> findByClient_EmailOrderByCreatedAtDesc(String email);

    /**
     * Находит все аренды с указанным статусом.
     *
     * @param status статус аренды (PENDING_PAYMENT, PAID, CANCELLED)
     * @return список аренд с данным статусом
     */
    List<Rental> findByStatus(String status);
}
