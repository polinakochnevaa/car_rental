package com.example.car_rental.controller;

import com.example.car_rental.model.Car;
import com.example.car_rental.model.Rental;
import com.example.car_rental.model.User;
import com.example.car_rental.repository.CarRepository;
import com.example.car_rental.repository.RentalRepository;
import com.example.car_rental.repository.UserRepository;
import com.example.car_rental.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Главный контроллер приложения.
 * <p>
 * Обрабатывает запросы к главной странице и перенаправляет пользователей
 * на соответствующую домашнюю страницу в зависимости от роли:
 * <ul>
 *     <li>Незалогиненные пользователи → лендинг страница (index.html)</li>
 *     <li>ROLE_USER → пользовательская панель (user/index.html)</li>
 *     <li>ROLE_ADMIN → административная панель со статистикой (admin/index.html)</li>
 * </ul>
 * <p>
 * Для администраторов предоставляет статистику:
 * <ul>
 *     <li>Общее количество автомобилей</li>
 *     <li>Общее количество пользователей</li>
 *     <li>Общий доход с оплаченных аренд</li>
 *     <li>Распределение автомобилей по статусам (для круговой диаграммы)</li>
 * </ul>
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Controller
public class MainController {

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Репозиторий для работы с автомобилями.
     */
    private final CarRepository carRepository;

    /**
     * Репозиторий для работы с пользователями.
     */
    private final UserRepository userRepository;

    /**
     * Репозиторий для работы с арендами.
     */
    private final RentalRepository rentalRepository;

    /**
     * Конструктор главного контроллера.
     *
     * @param userService       сервис для работы с пользователями
     * @param carRepository     репозиторий для работы с автомобилями
     * @param userRepository    репозиторий для работы с пользователями
     * @param rentalRepository  репозиторий для работы с арендами
     */
    public MainController(UserService userService, CarRepository carRepository,
                          UserRepository userRepository, RentalRepository rentalRepository) {
        this.userService = userService;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    /**
     * Отображает главную страницу приложения.
     * <p>
     * Перенаправляет пользователя на соответствующую домашнюю страницу
     * в зависимости от статуса аутентификации и роли:
     * <ul>
     *     <li>Для администраторов - панель управления со статистикой</li>
     *     <li>Для пользователей - пользовательская панель</li>
     *     <li>Для неаутентифицированных - лендинг страница</li>
     * </ul>
     *
     * @param authentication объект аутентификации текущего пользователя
     * @param model          модель для передачи данных в представление
     * @return имя шаблона для отображения
     */
    @GetMapping({"/", "/user"})
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                // Статистика для админской панели
                long totalCars = carRepository.count();
                long totalUsers = userRepository.countByRole("ROLE_USER");

                // Подсчёт доходов на основе оплаченных аренд
                List<Rental> allRentals = rentalRepository.findAll();
                long totalRevenue = allRentals.stream()
                        .filter(r -> "PAID".equals(r.getStatus()))
                        .mapToLong(r -> r.getTotalPrice() != null ? r.getTotalPrice() : 0)
                        .sum();

                // Подсчёт машин по статусам для круговой диаграммы
                List<Car> allCars = carRepository.findAll();
                Map<String, Long> statusCounts = allCars.stream()
                        .collect(Collectors.groupingBy(
                                car -> car.getStatus() != null ? car.getStatus() : "UNKNOWN",
                                Collectors.counting()
                        ));

                model.addAttribute("totalCars", totalCars);
                model.addAttribute("totalUsers", totalUsers);
                model.addAttribute("totalRevenue", totalRevenue);
                model.addAttribute("statusCounts", statusCounts);

                return "admin/index";  // шаблон admin/index.html для админа
            } else {
                // Получаем данные пользователя для отображения имени
                User user = userService.getUserByEmail(authentication.getName());
                model.addAttribute("userName", user.getFirstName());
                return "user/index";  // шаблон user/index.html для пользователя
            }
        }
        return "index"; // если не аутентифицирован, показываем лендинг
    }
}
