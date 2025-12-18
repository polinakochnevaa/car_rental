package com.example.car_rental.model;

import jakarta.persistence.*;

/**
 * Entity класс для представления автомобиля в системе аренды.
 * <p>
 * Содержит полную информацию об автомобиле: государственный номер, год выпуска,
 * цвет, стоимость аренды за день, текущий статус и город расположения.
 * Каждый автомобиль связан с определенной маркой и моделью.
 * <p>
 * Возможные статусы автомобиля:
 * <ul>
 *     <li>AVAILABLE - свободен для аренды</li>
 *     <li>RENTED - сдан в аренду</li>
 *     <li>RESERVED - зарезервирован клиентом</li>
 *     <li>MAINTENANCE - на обслуживании</li>
 * </ul>
 * <p>
 * Цена хранится в копейках для точности расчетов (для отображения делится на 100).
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Entity
@Table(name = "cars")
public class Car {

    /**
     * Уникальный идентификатор автомобиля
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Государственный номер автомобиля
     */
    @Column(name = "license_plate", length = 255, nullable = false)
    private String licensePlate;

    /**
     * Год выпуска автомобиля
     */
    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;

    /**
     * Цвет автомобиля
     */
    @Column(length = 255)
    private String color;

    /**
     * Стоимость аренды за день в копейках (для отображения делить на 100)
     */
    @Column(name = "price_per_day")
    private Integer pricePerDay;

    /**
     * Текущий статус автомобиля (AVAILABLE, RENTED, RESERVED, MAINTENANCE)
     */
    @Column(length = 255)
    private String status;

    /**
     * Город расположения автомобиля
     */
    @Column(length = 255)
    private String city;

    /**
     * Марка автомобиля
     */
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    /**
     * Модель автомобиля
     */
    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    /**
     * Возвращает уникальный идентификатор автомобиля.
     *
     * @return ID автомобиля
     */
    public Long getId() { return id; }

    /**
     * Устанавливает уникальный идентификатор автомобиля.
     *
     * @param id ID автомобиля
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Возвращает государственный номер автомобиля.
     *
     * @return государственный номер
     */
    public String getLicensePlate() { return licensePlate; }

    /**
     * Устанавливает государственный номер автомобиля.
     *
     * @param licensePlate государственный номер
     */
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    /**
     * Возвращает год выпуска автомобиля.
     *
     * @return год выпуска
     */
    public Integer getYearOfManufacture() { return yearOfManufacture; }

    /**
     * Устанавливает год выпуска автомобиля.
     *
     * @param yearOfManufacture год выпуска
     */
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }

    /**
     * Возвращает цвет автомобиля.
     *
     * @return цвет автомобиля
     */
    public String getColor() { return color; }

    /**
     * Устанавливает цвет автомобиля.
     *
     * @param color цвет автомобиля
     */
    public void setColor(String color) { this.color = color; }

    /**
     * Возвращает стоимость аренды за день в копейках.
     *
     * @return стоимость аренды за день
     */
    public Integer getPricePerDay() { return pricePerDay; }

    /**
     * Устанавливает стоимость аренды за день в копейках.
     *
     * @param pricePerDay стоимость аренды за день
     */
    public void setPricePerDay(Integer pricePerDay) { this.pricePerDay = pricePerDay; }

    /**
     * Возвращает текущий статус автомобиля.
     *
     * @return статус автомобиля
     */
    public String getStatus() { return status; }

    /**
     * Устанавливает текущий статус автомобиля.
     *
     * @param status статус автомобиля (AVAILABLE, RENTED, RESERVED, MAINTENANCE)
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Возвращает марку автомобиля.
     *
     * @return объект марки
     */
    public Brand getBrand() { return brand; }

    /**
     * Устанавливает марку автомобиля.
     *
     * @param brand объект марки
     */
    public void setBrand(Brand brand) { this.brand = brand; }

    /**
     * Возвращает модель автомобиля.
     *
     * @return объект модели
     */
    public Model getModel() { return model; }

    /**
     * Устанавливает модель автомобиля.
     *
     * @param model объект модели
     */
    public void setModel(Model model) { this.model = model; }

    /**
     * Возвращает город расположения автомобиля.
     *
     * @return город расположения
     */
    public String getCity() { return city; }

    /**
     * Устанавливает город расположения автомобиля.
     *
     * @param city город расположения
     */
    public void setCity(String city) { this.city = city; }
}
