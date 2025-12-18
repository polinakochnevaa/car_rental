package com.example.car_rental.controller.admin;

import com.example.car_rental.model.User;
import com.example.car_rental.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер администратора для управления пользователями.
 * <p>
 * Предоставляет функционал для администраторов (ROLE_ADMIN):
 * <ul>
 *     <li>Просмотр списка всех пользователей с фильтрацией</li>
 *     <li>Редактирование роли пользователя</li>
 *     <li>Удаление пользователя</li>
 * </ul>
 * <p>
 * Поддерживаемые фильтры:
 * <ul>
 *     <li>По email (частичное совпадение)</li>
 *     <li>По роли (ROLE_USER, ROLE_ADMIN)</li>
 * </ul>
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Конструктор контроллера пользователей администратора.
     *
     * @param userService сервис для работы с пользователями
     */
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Отображает список всех пользователей с фильтрацией.
     *
     * @param email email для фильтрации (опционально)
     * @param role  роль для фильтрации (опционально)
     * @param model модель для передачи данных в представление
     * @return имя шаблона admin/users/list
     */
    @GetMapping
    public String listUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            Model model) {

        List<User> users = userService.getUsersFiltered(email, role);
        model.addAttribute("users", users);
        model.addAttribute("email", email);
        model.addAttribute("role", role);
        return "admin/users/list";
    }

    /**
     * Отображает форму редактирования роли пользователя.
     *
     * @param id    идентификатор пользователя для редактирования
     * @param model модель для передачи данных в представление
     * @return имя шаблона admin/users/edit или перенаправление на список пользователей
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/users/edit";
    }

    /**
     * Обрабатывает обновление роли пользователя.
     * <p>
     * Изменяется только роль пользователя, остальные данные не затрагиваются.
     *
     * @param user данные пользователя с новой ролью
     * @return перенаправление на список пользователей
     */
    @PostMapping("/edit")
    public String updateUserRole(@ModelAttribute User user) {
        userService.updateUserRoleOnly(user.getId(), user.getRole());
        return "redirect:/admin/users";
    }

    /**
     * Удаляет пользователя из системы.
     *
     * @param id идентификатор удаляемого пользователя
     * @return перенаправление на список пользователей
     */
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
