package com.example.car_rental.controller.user;

import com.example.car_rental.model.User;
import com.example.car_rental.repository.UserRepository;
import com.example.car_rental.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/profile")
public class UserProfileController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserProfileController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/my")
    public String profileForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "user/profile/my";
    }

    @PostMapping
    public String updateProfile(@ModelAttribute User user, @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        // Получаем текущего пользователя из базы
        User existingUser = userService.getUserByEmail(userDetails.getUsername());

        if (existingUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь не найден");
            return "redirect:/user/profile/my";
        }

        // Обновляем только разрешенные поля
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setMiddleName(user.getMiddleName());
        existingUser.setPhone(user.getPhone());
        existingUser.setPassportSeries(user.getPassportSeries());
        existingUser.setPassportNumber(user.getPassportNumber());
        existingUser.setDriverLicenseSeries(user.getDriverLicenseSeries());
        existingUser.setDriverLicenseNumber(user.getDriverLicenseNumber());
        // Email, дата рождения, пароль и роль не обновляются

        try {
            // Сохраняем напрямую через репозиторий, чтобы не затрагивать пароль
            userRepository.save(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Профиль успешно обновлен");
            return "redirect:/user/profile/my";
        } catch (DataIntegrityViolationException e) {
            String errorMessage = getErrorMessage(e);
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            redirectAttributes.addFlashAttribute("user", existingUser);
            return "redirect:/user/profile/my";
        }
    }

    private String getErrorMessage(DataIntegrityViolationException e) {
        String message = e.getMessage().toLowerCase();

        if (message.contains("unique_passport") || message.contains("passport")) {
            return "Паспорт с такими серией и номером уже зарегистрирован в системе";
        } else if (message.contains("driver_license") || message.contains("водительск")) {
            return "Водительское удостоверение с такими серией и номером уже зарегистрировано в системе";
        } else if (message.contains("phone") || message.contains("телефон")) {
            return "Телефон уже используется другим пользователем";
        } else if (message.contains("email")) {
            return "Email уже используется другим пользователем";
        } else {
            return "Не удалось обновить профиль. Проверьте введенные данные.";
        }
    }
}
