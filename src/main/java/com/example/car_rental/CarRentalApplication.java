package com.example.car_rental;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс приложения для системы аренды автомобилей "ИжДрайв".
 * <p>
 * Это веб-приложение на Spring Boot для управления арендой автомобилей,
 * включающее:
 * <ul>
 *     <li>Регистрацию и аутентификацию пользователей</li>
 *     <li>Каталог доступных автомобилей с фильтрацией и сортировкой</li>
 *     <li>Систему бронирования и оплаты аренды</li>
 *     <li>Административную панель для управления автомобилями, моделями, марками и пользователями</li>
 *     <li>Автоматическую отмену неоплаченных бронирований</li>
 * </ul>
 * <p>
 * Приложение использует Spring Boot, Spring Security, Spring Data JPA,
 * Thymeleaf для шаблонов и PostgreSQL для хранения данных.
 *
 * @author ИжДрайв
 * @version 1.0
 */
@SpringBootApplication
public class CarRentalApplication {
	/**
	 * Точка входа в приложение.
	 * Запускает Spring Boot приложение и инициализирует все компоненты.
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(CarRentalApplication.class, args);
	}
}