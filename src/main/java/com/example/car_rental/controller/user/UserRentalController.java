package com.example.car_rental.controller.user;

import com.example.car_rental.model.Car;
import com.example.car_rental.model.Rental;
import com.example.car_rental.service.CarService;
import com.example.car_rental.service.RentalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/user/rentals")
public class UserRentalController {

    private final RentalService rentalService;
    private final CarService carService;

    public UserRentalController(RentalService rentalService, CarService carService) {
        this.rentalService = rentalService;
        this.carService = carService;
    }

    @GetMapping("/my")
    public String listMyRentals(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        var rentals = rentalService.getRentalsByClientEmail(userDetails.getUsername());
        model.addAttribute("rentals", rentals);
        return "user/rentals/my";
    }

    @GetMapping("/add")
    public String showAddRentalForm(@RequestParam("carId") Long carId, Model model) {
        Car car = carService.getCarById(carId);
        if (car == null || !"AVAILABLE".equals(car.getStatus())) {
            return "redirect:/user/cars";
        }
        model.addAttribute("car", car);
        model.addAttribute("rental", new Rental());
        return "user/rentals/add";
    }

    @PostMapping("/add")
    public String addRental(@ModelAttribute Rental rental, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate startDate = rental.getStartDate();

        // Проверяем что дата начала - это завтра
        if (startDate == null || !startDate.isEqual(tomorrow)) {
            model.addAttribute("error", "Дата начала аренды должна быть завтра.");
            Car car = carService.getCarById(rental.getCar().getId());
            model.addAttribute("car", car);
            return "user/rentals/add";
        }

        Car car = carService.getCarById(rental.getCar().getId());
        if (car == null || !"AVAILABLE".equals(car.getStatus())) {
            model.addAttribute("error", "Автомобиль недоступен для аренды.");
            model.addAttribute("car", car);
            return "user/rentals/add";
        }

        if (rental.getEndDate() == null || rental.getEndDate().isBefore(startDate) || rental.getEndDate().isEqual(startDate)) {
            model.addAttribute("error", "Дата окончания аренды должна быть позже даты начала.");
            model.addAttribute("car", car);
            return "user/rentals/add";
        }

        long days = rental.getEndDate().toEpochDay() - startDate.toEpochDay();
        // Расчет в копейках: цена за день (коп) * количество дней
        rental.setTotalPrice((int) (car.getPricePerDay() * days));

        rental.setClient(null); // заполняется в сервисе
        rental.setCar(car);
        rental.setId(null);
        rental.setStatus("PENDING_PAYMENT");

        Rental createdRental = rentalService.createRental(rental, userDetails.getUsername());

        car.setStatus("RESERVED");
        carService.saveCar(car);

        // Перенаправляем на страницу оплаты
        return "redirect:/user/payments/process?rentalId=" + createdRental.getId();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/cancel/{rentalId}")
    public String cancelRental(@PathVariable Long rentalId, @AuthenticationPrincipal UserDetails userDetails) {
        rentalService.cancelRental(rentalId);
        return "redirect:/user/rentals/my";
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/pay/{rentalId}")
    public String payRental(@PathVariable Long rentalId, @AuthenticationPrincipal UserDetails userDetails) {
        rentalService.confirmPayment(rentalId);
        return "redirect:/user/rentals/my";
    }
}
