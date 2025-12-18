package com.example.car_rental.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity класс для представления аренды автомобиля.
 * <p>
 * Содержит всю информацию о транзакции аренды: клиент, автомобиль,
 * даты начала и окончания аренды, общая стоимость, статус аренды и время создания заказа.
 * <p>
 * Возможные статусы аренды:
 * <ul>
 *     <li>PENDING_PAYMENT - ожидает оплаты (резервирует автомобиль на 15 минут)</li>
 *     <li>PAID - оплачена (автомобиль арендован)</li>
 *     <li>CANCELLED - отменена клиентом или системой (при истечении времени оплаты)</li>
 * </ul>
 * <p>
 * Общая стоимость хранится в копейках для точности расчетов.
 * Автоматическая отмена неоплаченных аренд происходит через 15 минут после создания.
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Entity
@Table(name = "rentals")
public class Rental {

    /**
     * Уникальный идентификатор аренды
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Клиент, арендующий автомобиль
     */
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    /**
     * Арендуемый автомобиль
     */
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    /**
     * Дата начала аренды
     */
    @Column(name = "start_date")
    private LocalDate startDate;

    /**
     * Дата окончания аренды
     */
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * Общая стоимость аренды в копейках
     */
    @Column(name = "total_price")
    private Integer totalPrice;

    /**
     * Текущий статус аренды (PENDING_PAYMENT, PAID, CANCELLED)
     */
    @Column(length = 50)
    private String status;

    /**
     * Дата и время создания заказа на аренду
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Возвращает уникальный идентификатор аренды.
     *
     * @return ID аренды
     */
    public Long getId() { return id; }

    /**
     * Устанавливает уникальный идентификатор аренды.
     *
     * @param id ID аренды
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Возвращает клиента, арендующего автомобиль.
     *
     * @return объект клиента
     */
    public User getClient() { return client; }

    /**
     * Устанавливает клиента для данной аренды.
     *
     * @param client объект клиента
     */
    public void setClient(User client) { this.client = client; }

    /**
     * Возвращает арендуемый автомобиль.
     *
     * @return объект автомобиля
     */
    public Car getCar() { return car; }

    /**
     * Устанавливает автомобиль для данной аренды.
     *
     * @param car объект автомобиля
     */
    public void setCar(Car car) { this.car = car; }

    /**
     * Возвращает дату начала аренды.
     *
     * @return дата начала аренды
     */
    public LocalDate getStartDate() { return startDate; }

    /**
     * Устанавливает дату начала аренды.
     *
     * @param startDate дата начала аренды
     */
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    /**
     * Возвращает дату окончания аренды.
     *
     * @return дата окончания аренды
     */
    public LocalDate getEndDate() { return endDate; }

    /**
     * Устанавливает дату окончания аренды.
     *
     * @param endDate дата окончания аренды
     */
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    /**
     * Возвращает общую стоимость аренды в копейках.
     *
     * @return общая стоимость аренды
     */
    public Integer getTotalPrice() { return totalPrice; }

    /**
     * Устанавливает общую стоимость аренды в копейках.
     *
     * @param totalPrice общая стоимость аренды
     */
    public void setTotalPrice(Integer totalPrice) { this.totalPrice = totalPrice; }

    /**
     * Возвращает текущий статус аренды.
     *
     * @return статус аренды
     */
    public String getStatus() { return status; }

    /**
     * Устанавливает текущий статус аренды.
     *
     * @param status статус аренды (PENDING_PAYMENT, PAID, CANCELLED)
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Возвращает дату и время создания заказа.
     *
     * @return дата и время создания
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Устанавливает дату и время создания заказа.
     *
     * @param createdAt дата и время создания
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
