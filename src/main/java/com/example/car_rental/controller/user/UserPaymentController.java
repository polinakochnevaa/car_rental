package com.example.car_rental.controller.user;

import com.example.car_rental.model.Rental;
import com.example.car_rental.service.RentalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для обработки платежей пользователя.
 * <p>
 * Предоставляет функционал оплаты аренды для пользователей с ролью ROLE_USER.
 * Обрабатывает ввод данных банковской карты и подтверждение оплаты.
 * <p>
 * Процесс оплаты:
 * <ol>
 *     <li>Отображение формы ввода данных карты для аренды со статусом PENDING_PAYMENT</li>
 *     <li>Валидация данных карты (номер карты должен содержать минимум 16 цифр)</li>
 *     <li>Подтверждение оплаты (перевод аренды в статус PAID, автомобиля - в RENTED)</li>
 *     <li>Перенаправление на страницу "Мои аренды" с сообщением об успешной оплате</li>
 * </ol>
 * <p>
 * Безопасность:
 * <ul>
 *     <li>Пользователь может оплатить только свою аренду</li>
 *     <li>Оплата доступна только для аренд со статусом PENDING_PAYMENT</li>
 * </ul>
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Controller
@RequestMapping("/user/payments")
@PreAuthorize("hasRole('USER')")
public class UserPaymentController {

    /**
     * Сервис для работы с арендами.
     */
    private final RentalService rentalService;

    /**
     * Конструктор контроллера платежей пользователя.
     *
     * @param rentalService сервис для работы с арендами
     */
    public UserPaymentController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * Отображает страницу оплаты аренды.
     * <p>
     * Проверяет, что аренда принадлежит текущему пользователю
     * и имеет статус PENDING_PAYMENT (ожидает оплаты).
     *
     * @param rentalId    идентификатор аренды для оплаты
     * @param model       модель для передачи данных в представление
     * @param userDetails данные аутентифицированного пользователя
     * @return имя шаблона user/payments/process или перенаправление на список аренд
     */
    @GetMapping("/process")
    public String showPaymentPage(@RequestParam("rentalId") Long rentalId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Rental rental = rentalService.getRentalById(rentalId);

        if (rental == null) {
            return "redirect:/user/rentals/my";
        }

        // Проверяем что аренда принадлежит текущему пользователю
        if (!rental.getClient().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/user/rentals/my";
        }

        // Проверяем что аренда еще не оплачена
        if (!"PENDING_PAYMENT".equals(rental.getStatus())) {
            return "redirect:/user/rentals/my";
        }

        model.addAttribute("rental", rental);
        return "user/payments/process";
    }

    /**
     * Обрабатывает оплату аренды.
     * <p>
     * Выполняет базовую валидацию данных карты (номер карты должен содержать минимум 16 цифр).
     * При успешной оплате переводит аренду в статус PAID и автомобиль в статус RENTED.
     *
     * @param rentalId    идентификатор оплачиваемой аренды
     * @param cardNumber  номер банковской карты
     * @param cardHolder  имя держателя карты
     * @param expiryDate  срок действия карты
     * @param cvv         CVV-код карты
     * @param userDetails данные аутентифицированного пользователя
     * @param model       модель для передачи сообщений об ошибках
     * @return перенаправление на список аренд при успехе или форму оплаты при ошибке
     */
    @PostMapping("/process")
    public String processPayment(@RequestParam("rentalId") Long rentalId,
                                  @RequestParam("cardNumber") String cardNumber,
                                  @RequestParam("cardHolder") String cardHolder,
                                  @RequestParam("expiryDate") String expiryDate,
                                  @RequestParam("cvv") String cvv,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {

        Rental rental = rentalService.getRentalById(rentalId);

        if (rental == null || !rental.getClient().getEmail().equals(userDetails.getUsername())) {
            return "redirect:/user/rentals/my";
        }

        // Простая валидация карты
        if (cardNumber == null || cardNumber.length() < 16) {
            model.addAttribute("error", "Неверный номер карты");
            model.addAttribute("rental", rental);
            return "user/payments/process";
        }

        // Подтверждаем оплату
        rentalService.confirmPayment(rentalId);

        return "redirect:/user/rentals/my?paymentSuccess=true";
    }
}
