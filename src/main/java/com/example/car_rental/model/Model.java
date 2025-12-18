package com.example.car_rental.model;

import jakarta.persistence.*;

/**
 * Entity класс для представления модели автомобиля.
 * <p>
 * Содержит название модели (например, Camry, X5, Vesta) и связь с маркой автомобиля.
 * Каждая модель принадлежит определенной марке, что позволяет организовать
 * иерархическую структуру марка-модель для автомобилей.
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Entity
@Table(name = "models")
public class Model {

    /**
     * Уникальный идентификатор модели
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название модели автомобиля (например, Camry, X5, Vesta)
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * Марка, к которой принадлежит данная модель
     */
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    /**
     * Возвращает уникальный идентификатор модели.
     *
     * @return ID модели
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает уникальный идентификатор модели.
     *
     * @param id ID модели
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает название модели автомобиля.
     *
     * @return название модели
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название модели автомобиля.
     *
     * @param name название модели
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает марку, к которой принадлежит модель.
     *
     * @return объект марки
     */
    public Brand getBrand() {
        return brand;
    }

    /**
     * Устанавливает марку для данной модели.
     *
     * @param brand объект марки
     */
    public void setBrand(Brand brand) {
        this.brand = brand;
    }
}
