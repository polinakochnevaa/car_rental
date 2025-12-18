package com.example.car_rental.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entity класс для представления марки автомобиля.
 * <p>
 * Содержит название марки (например, Toyota, BMW, Lada) и связь с моделями автомобилей.
 * Используется для категоризации автомобилей в системе аренды.
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Entity
@Table(name = "brands")
public class Brand {

    /**
     * Уникальный идентификатор марки
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название марки автомобиля (например, Toyota, BMW, Lada)
     */
    @Column(nullable = false, unique = true, length = 255)
    private String name;

    /**
     * Список моделей автомобилей данной марки
     */
    @OneToMany(mappedBy = "brand")
    private List<Model> models;

    /**
     * Возвращает уникальный идентификатор марки.
     *
     * @return ID марки
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает уникальный идентификатор марки.
     *
     * @param id ID марки
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Возвращает название марки автомобиля.
     *
     * @return название марки
     */
    public String getName() {
        return name;
    }

    /**
     * Устанавливает название марки автомобиля.
     *
     * @param name название марки
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает список моделей данной марки.
     *
     * @return список моделей
     */
    public List<Model> getModels() {
        return models;
    }

    /**
     * Устанавливает список моделей для данной марки.
     *
     * @param models список моделей
     */
    public void setModels(List<Model> models) {
        this.models = models;
    }
}
