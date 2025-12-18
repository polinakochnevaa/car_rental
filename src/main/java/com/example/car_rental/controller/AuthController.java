package com.example.car_rental.controller;

import com.example.car_rental.model.User;
import com.example.car_rental.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    private boolean isCyrillic(String value) {
        return value != null && value.matches("^[А-ЯЁа-яё\\s-]+$");
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";  // шаблон auth/register.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (user.getBirthDate() == null) {
            model.addAttribute("error", "Дата рождения обязательна");
            return "auth/register";
        }
        if (Period.between(user.getBirthDate(), LocalDate.now()).getYears() < 18) {
            model.addAttribute("error", "Вам должно быть не менее 18 лет для регистрации");
            return "auth/register";
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            model.addAttribute("error", "Email обязателен");
            return "auth/register";
        }
        if (!userService.isPasswordStrong(user.getPassword())) {
            model.addAttribute("error", "Пароль должен быть не менее 8 символов, содержать цифры, заглавные буквы и спецсимволы, и не иметь более 3 повторяющихся подряд символов");
            return "auth/register";
        }
        if (!userService.isPhoneValid(user.getPhone())) {
            model.addAttribute("error", "Телефон должен начинаться с +7 и содержать 11 цифр");
            return "auth/register";
        }
        if (!userService.isDriverLicenseSeriesValid(user.getDriverLicenseSeries())) {
            model.addAttribute("error", "Серия водительского удостоверения должна содержать 4 цифры");
            return "auth/register";
        }
        if (!userService.isDriverLicenseNumberValid(user.getDriverLicenseNumber())) {
            model.addAttribute("error", "Номер водительского удостоверения должен содержать 6 цифр");
            return "auth/register";
        }
        if (!userService.isPassportSeriesValid(user.getPassportSeries())) {
            model.addAttribute("error", "Серия паспорта должна содержать 4 цифры");
            return "auth/register";
        }
        if (!userService.isPassportNumberValid(user.getPassportNumber())) {
            model.addAttribute("error", "Номер паспорта должен содержать 6 цифр");
            return "auth/register";
        }

        if (!isCyrillic(user.getLastName())) {
            model.addAttribute("error", "Фамилия должна содержать только кириллицу");
            return "auth/register";
        }
        if (!isCyrillic(user.getFirstName())) {
            model.addAttribute("error", "Имя должно содержать только кириллицу");
            return "auth/register";
        }
        if (user.getMiddleName() != null && !user.getMiddleName().isEmpty() && !isCyrillic(user.getMiddleName())) {
            model.addAttribute("error", "Отчество должно содержать только кириллицу");
            return "auth/register";
        }

        StringBuilder errorBuilder = new StringBuilder();

        if (userService.getUserByEmail(user.getEmail()) != null) {
            errorBuilder.append("Email уже используется. ");
        }
        if (userService.existsByPhone(user.getPhone())) {
            errorBuilder.append("Телефон уже используется. ");
        }
        if (userService.existsByDriverLicenseSeriesAndNumber(user.getDriverLicenseSeries(), user.getDriverLicenseNumber())) {
            errorBuilder.append("Водительское удостоверение уже используется. ");
        }
        if (userService.existsByPassportSeriesAndPassportNumber(user.getPassportSeries(), user.getPassportNumber())) {
            errorBuilder.append("Паспорт уже используется. ");
        }

        if (errorBuilder.length() > 0) {
            model.addAttribute("error", errorBuilder.toString().trim());
            return "auth/register";
        }

        user.setRole("ROLE_USER");
        userService.saveUser(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "registered", required = false) String registered,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
        }
        if (registered != null) {
            model.addAttribute("message", "Регистрация прошла успешно. Войдите в систему.");
        }
        return "auth/login";  // шаблон auth/login.html
    }
}
