package com.example.car_rental.controller.admin;

import com.example.car_rental.model.User;
import com.example.car_rental.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

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

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/users/edit";
    }

    @PostMapping("/edit")
    public String updateUserRole(@ModelAttribute User user) {
        userService.updateUserRoleOnly(user.getId(), user.getRole());
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
