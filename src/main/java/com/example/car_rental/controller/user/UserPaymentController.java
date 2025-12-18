package com.example.car_rental.controller.user;

import com.example.car_rental.model.Rental;
import com.example.car_rental.service.RentalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/payments")
@PreAuthorize("hasRole('USER')")
public class UserPaymentController {

    private final RentalService rentalService;

    public UserPaymentController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

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
