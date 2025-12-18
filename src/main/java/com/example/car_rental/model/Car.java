package com.example.car_rental.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", length = 255, nullable = false)
    private String licensePlate;

    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;

    @Column(length = 255)
    private String color;

    @Column(name = "price_per_day")
    private Integer pricePerDay;  // Цена в копейках (для отображения делить на 100)

    @Column(length = 255)
    private String status;

    @Column(length = 255)
    private String city;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    // геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public Integer getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Integer getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(Integer pricePerDay) { this.pricePerDay = pricePerDay; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }
    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
