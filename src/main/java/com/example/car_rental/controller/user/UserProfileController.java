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

/**
 * Контроллер для управления профилем пользователя.
 * <p>
 * Предоставляет функционал для просмотра и редактирования личных данных
 * текущего аутентифицированного пользователя (ROLE_USER).
 * <p>
 * Пользователь может обновить следующие данные:
 * <ul>
 *     <li>ФИО (фамилия, имя, отчество)</li>
 *     <li>Телефон</li>
 *     <li>Паспортные данные (серия и номер)</li>
 *     <li>Водительское удостоверение (серия и номер)</li>
 * </ul>
 * <p>
 * Email, дата рождения, пароль и роль НЕ могут быть изменены через этот контроллер.
 * Включает обработку ошибок уникальности данных (телефон, паспорт, водительское удостоверение).
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Controller
@RequestMapping("/user/profile")
public class UserProfileController {

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Репозиторий для работы с пользователями.
     */
    private final UserRepository userRepository;

    /**
     * Конструктор контроллера профиля пользователя.
     *
     * @param userService    сервис для работы с пользователями
     * @param userRepository репозиторий для работы с пользователями
     */
    public UserProfileController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    /**
     * Отображает форму редактирования профиля текущего пользователя.
     *
     * @param model       модель для передачи данных в представление
     * @param userDetails данные аутентифицированного пользователя
     * @return имя шаблона user/profile/my
     */
    @GetMapping("/my")
    public String profileForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "user/profile/my";
    }

    /**
     * Обрабатывает обновление профиля пользователя.
     * <p>
     * Обновляет только разрешенные поля: ФИО, телефон, паспортные данные
     * и данные водительского удостоверения. Email, дата рождения, пароль
     * и роль НЕ изменяются через этот метод.
     *
     * @param user                 данные для обновления профиля
     * @param userDetails          данные аутентифицированного пользователя
     * @param redirectAttributes   атрибуты для передачи flash-сообщений
     * @return перенаправление на страницу профиля
     */
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

    /**
     * Извлекает понятное пользователю сообщение об ошибке из исключения.
     * <p>
     * Анализирует текст исключения DataIntegrityViolationException
     * и возвращает соответствующее пользовательское сообщение.
     *
     * @param e исключение нарушения целостности данных
     * @return понятное пользователю сообщение об ошибке
     */
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
