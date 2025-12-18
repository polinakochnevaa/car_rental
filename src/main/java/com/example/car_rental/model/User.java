package com.example.car_rental.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entity класс для представления пользователя системы.
 * <p>
 * Содержит личные данные пользователя, включая контактную информацию,
 * документы (паспорт, водительское удостоверение) и роль в системе.
 * Пользователь может быть обычным клиентом (ROLE_USER) или администратором (ROLE_ADMIN).
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"driver_license_series", "driver_license_number"}),
                @UniqueConstraint(columnNames = {"passport_series", "passport_number"}),
                @UniqueConstraint(columnNames = {"email"}),
                @UniqueConstraint(columnNames = {"phone"})
        }
)
public class User {

    /**
     * Уникальный идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email пользователя (используется для входа в систему)
     */
    @Column(nullable = false)
    private String email;

    /**
     * Хешированный пароль пользователя
     */
    @Column(nullable = false)
    private String password;

    /**
     * Имя пользователя
     */
    @Column(length = 255)
    private String firstName;

    /**
     * Фамилия пользователя
     */
    @Column(length = 255)
    private String lastName;

    /**
     * Отчество пользователя
     */
    @Column(length = 255)
    private String middleName;

    /**
     * Серия водительского удостоверения (4 цифры)
     */
    @Column(length = 4)
    private String driverLicenseSeries;

    /**
     * Номер водительского удостоверения (6 цифр)
     */
    @Column(length = 6)
    private String driverLicenseNumber;

    /**
     * Серия паспорта (4 цифры)
     */
    @Column(length = 4)
    private String passportSeries;

    /**
     * Номер паспорта (6 цифр)
     */
    @Column(length = 6)
    private String passportNumber;

    /**
     * Номер телефона пользователя
     */
    @Column
    private String phone;

    /**
     * Дата рождения пользователя
     */
    @Column
    private LocalDate birthDate;

    /**
     * Роль пользователя в системе (ROLE_USER или ROLE_ADMIN)
     */
    @Column(nullable = false)
    private String role;

    /**
     * Возвращает уникальный идентификатор пользователя.
     *
     * @return ID пользователя
     */
    public Long getId() { return id; }

    /**
     * Устанавливает уникальный идентификатор пользователя.
     *
     * @param id ID пользователя
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Возвращает email пользователя.
     *
     * @return email пользователя
     */
    public String getEmail() { return email; }

    /**
     * Устанавливает email пользователя.
     *
     * @param email email пользователя
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Возвращает хешированный пароль пользователя.
     *
     * @return пароль пользователя
     */
    public String getPassword() { return password; }

    /**
     * Устанавливает пароль пользователя.
     *
     * @param password пароль пользователя (будет захеширован)
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя
     */
    public String getFirstName() { return firstName; }

    /**
     * Устанавливает имя пользователя.
     *
     * @param firstName имя пользователя
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Возвращает фамилию пользователя.
     *
     * @return фамилия пользователя
     */
    public String getLastName() { return lastName; }

    /**
     * Устанавливает фамилию пользователя.
     *
     * @param lastName фамилия пользователя
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Возвращает отчество пользователя.
     *
     * @return отчество пользователя
     */
    public String getMiddleName() { return middleName; }

    /**
     * Устанавливает отчество пользователя.
     *
     * @param middleName отчество пользователя
     */
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    /**
     * Возвращает серию водительского удостоверения.
     *
     * @return серия водительского удостоверения
     */
    public String getDriverLicenseSeries() { return driverLicenseSeries; }

    /**
     * Устанавливает серию водительского удостоверения.
     *
     * @param driverLicenseSeries серия водительского удостоверения (4 цифры)
     */
    public void setDriverLicenseSeries(String driverLicenseSeries) { this.driverLicenseSeries = driverLicenseSeries; }

    /**
     * Возвращает номер водительского удостоверения.
     *
     * @return номер водительского удостоверения
     */
    public String getDriverLicenseNumber() { return driverLicenseNumber; }

    /**
     * Устанавливает номер водительского удостоверения.
     *
     * @param driverLicenseNumber номер водительского удостоверения (6 цифр)
     */
    public void setDriverLicenseNumber(String driverLicenseNumber) { this.driverLicenseNumber = driverLicenseNumber; }

    /**
     * Возвращает серию паспорта.
     *
     * @return серия паспорта
     */
    public String getPassportSeries() { return passportSeries; }

    /**
     * Устанавливает серию паспорта.
     *
     * @param passportSeries серия паспорта (4 цифры)
     */
    public void setPassportSeries(String passportSeries) { this.passportSeries = passportSeries; }

    /**
     * Возвращает номер паспорта.
     *
     * @return номер паспорта
     */
    public String getPassportNumber() { return passportNumber; }

    /**
     * Устанавливает номер паспорта.
     *
     * @param passportNumber номер паспорта (6 цифр)
     */
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    /**
     * Возвращает номер телефона пользователя.
     *
     * @return номер телефона
     */
    public String getPhone() { return phone; }

    /**
     * Устанавливает номер телефона пользователя.
     *
     * @param phone номер телефона
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Возвращает дату рождения пользователя.
     *
     * @return дата рождения
     */
    public LocalDate getBirthDate() { return birthDate; }

    /**
     * Устанавливает дату рождения пользователя.
     *
     * @param birthDate дата рождения
     */
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    /**
     * Возвращает роль пользователя в системе.
     *
     * @return роль пользователя (ROLE_USER или ROLE_ADMIN)
     */
    public String getRole() { return role; }

    /**
     * Устанавливает роль пользователя в системе.
     *
     * @param role роль пользователя (ROLE_USER или ROLE_ADMIN)
     */
    public void setRole(String role) { this.role = role; }
}
