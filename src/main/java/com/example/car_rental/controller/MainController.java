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

@Controller
public class MainController {

    private final UserService userService;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public MainController(UserService userService, CarRepository carRepository,
                          UserRepository userRepository, RentalRepository rentalRepository) {
        this.userService = userService;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

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
