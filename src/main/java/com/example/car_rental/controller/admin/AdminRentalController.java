package com.example.car_rental.controller.admin;

import com.example.car_rental.model.Rental;
import com.example.car_rental.service.RentalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/rentals")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRentalController {

    private final RentalService rentalService;

    public AdminRentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public String listRentals(
            @RequestParam(required = false, defaultValue = "") String plate,
            @RequestParam(required = false, defaultValue = "") String email,
            @RequestParam(required = false, defaultValue = "") String statusFilter,
            @RequestParam(required = false, defaultValue = "createdAt") String sortField,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            Model model) {

        List<Rental> rentals = rentalService.getAllRentals().stream()
                .filter(rental -> plate == null || plate.isBlank() ||
                        (rental.getCar() != null && rental.getCar().getLicensePlate() != null &&
                         rental.getCar().getLicensePlate().toLowerCase().contains(plate.toLowerCase())))
                .filter(rental -> email == null || email.isBlank() ||
                        (rental.getClient() != null && rental.getClient().getEmail() != null &&
                         rental.getClient().getEmail().toLowerCase().contains(email.toLowerCase())))
                .filter(rental -> statusFilter == null || statusFilter.isBlank() ||
                        rental.getStatus().equals(statusFilter))
                .sorted(getComparator(sortField, sortDir))
                .collect(Collectors.toList());

        model.addAttribute("rentals", rentals);
        model.addAttribute("plate", plate);
        model.addAttribute("email", email);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        return "admin/rentals/list";
    }

    private Comparator<Rental> getComparator(String sortField, String sortDir) {
        Comparator<Rental> comparator = switch (sortField) {
            case "brand" -> Comparator.comparing(r -> r.getCar().getBrand().getName(), String.CASE_INSENSITIVE_ORDER);
            case "model" -> Comparator.comparing(r -> r.getCar().getModel().getName(), String.CASE_INSENSITIVE_ORDER);
            case "email" -> Comparator.comparing(r -> r.getClient().getEmail(), String.CASE_INSENSITIVE_ORDER);
            case "totalPrice" -> Comparator.comparingInt(Rental::getTotalPrice);
            case "startDate" -> Comparator.comparing(Rental::getStartDate);
            case "createdAt" -> Comparator.comparing(Rental::getCreatedAt);
            default -> Comparator.comparing(Rental::getCreatedAt);
        };
        return "desc".equalsIgnoreCase(sortDir) ? comparator.reversed() : comparator;
    }

    @PostMapping("/delete/{id}")
    public String deleteRental(@PathVariable Long id) {
        rentalService.cancelRental(id);
        return "redirect:/admin/rentals";
    }
}
