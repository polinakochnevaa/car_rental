package com.example.car_rental.controller.user;

import com.example.car_rental.model.Brand;
import com.example.car_rental.model.Car;
import com.example.car_rental.service.BrandService;
import com.example.car_rental.service.CarService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/cars")
public class UserCarController {

    private final CarService carService;
    private final BrandService brandService;

    public UserCarController(CarService carService, BrandService brandService) {
        this.carService = carService;
        this.brandService = brandService;
    }

    @GetMapping
    public String listCars(
            @RequestParam(name = "sortOrder", required = false, defaultValue = "default") String sortOrder,
            @RequestParam(name = "brandId", required = false) Long brandId,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "color", required = false) String color,
            @RequestParam(name = "city", required = false) String city,
            @RequestParam(name = "minPrice", required = false) Integer minPrice,
            @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
            Model model,
            @AuthenticationPrincipal UserDetails user) {

        // Получаем все автомобили, кроме тех, что на обслуживании
        List<Car> cars = carService.getAvailableCars().stream()
                .filter(car -> !"MAINTENANCE".equals(car.getStatus()))
                .collect(Collectors.toList());

        // Фильтрация по бренду
        if (brandId != null && brandId != 0) {
            cars = cars.stream()
                    .filter(car -> car.getBrand() != null && brandId.equals(car.getBrand().getId()))
                    .collect(Collectors.toList());
        }

        // Фильтрация по году
        if (year != null && year != 0) {
            cars = cars.stream()
                    .filter(car -> year.equals(car.getYearOfManufacture()))
                    .collect(Collectors.toList());
        }

        // Фильтрация по цвету
        if (color != null && !color.isBlank()) {
            cars = cars.stream()
                    .filter(car -> color.equalsIgnoreCase(car.getColor()))
                    .collect(Collectors.toList());
        }

        // Фильтрация по городу
        if (city != null && !city.isBlank()) {
            cars = cars.stream()
                    .filter(car -> city.equalsIgnoreCase(car.getCity()))
                    .collect(Collectors.toList());
        }

        // Фильтрация по цене (минимальная) - конвертируем рубли в копейки
        if (minPrice != null && minPrice > 0) {
            int minPriceInCents = minPrice * 100;
            cars = cars.stream()
                    .filter(car -> car.getPricePerDay() != null && car.getPricePerDay() >= minPriceInCents)
                    .collect(Collectors.toList());
        }

        // Фильтрация по цене (максимальная) - конвертируем рубли в копейки
        if (maxPrice != null && maxPrice > 0) {
            int maxPriceInCents = maxPrice * 100;
            cars = cars.stream()
                    .filter(car -> car.getPricePerDay() != null && car.getPricePerDay() <= maxPriceInCents)
                    .collect(Collectors.toList());
        }

        // Сортировка
        if ("priceAsc".equals(sortOrder)) {
            cars.sort((c1, c2) -> {
                if (c1.getPricePerDay() == null) return 1;
                if (c2.getPricePerDay() == null) return -1;
                return c1.getPricePerDay().compareTo(c2.getPricePerDay());
            });
        } else if ("priceDesc".equals(sortOrder)) {
            cars.sort((c1, c2) -> {
                if (c1.getPricePerDay() == null) return 1;
                if (c2.getPricePerDay() == null) return -1;
                return c2.getPricePerDay().compareTo(c1.getPricePerDay());
            });
        }

        // Получаем бренды только из отфильтрованных автомобилей
        Set<Brand> brands = cars.stream()
                .map(Car::getBrand)
                .filter(b -> b != null)
                .collect(Collectors.toSet());

        // Уникальные годы из отфильтрованных авто
        List<Integer> years = cars.stream()
                .map(Car::getYearOfManufacture)
                .filter(y -> y != null)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Уникальные цвета из отфильтрованных авто
        Set<String> colors = cars.stream()
                .map(Car::getColor)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.toSet());

        // Уникальные города из отфильтрованных авто
        Set<String> cities = cars.stream()
                .map(Car::getCity)
                .filter(c -> c != null && !c.isBlank())
                .collect(Collectors.toSet());

        // Вычисляем минимальную и максимальную цену из всех доступных авто (в рублях)
        List<Car> allCars = carService.getAvailableCars();
        Integer minPriceAvailable = allCars.stream()
                .map(Car::getPricePerDay)
                .filter(p -> p != null && p > 0)
                .min(Integer::compareTo)
                .map(p -> p / 100) // Конвертируем копейки в рубли
                .orElse(0);

        Integer maxPriceAvailable = allCars.stream()
                .map(Car::getPricePerDay)
                .filter(p -> p != null && p > 0)
                .max(Integer::compareTo)
                .map(p -> p / 100) // Конвертируем копейки в рубли
                .orElse(10000);

        model.addAttribute("cars", cars);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("brands", brands);
        model.addAttribute("years", years);
        model.addAttribute("colors", colors);
        model.addAttribute("cities", cities);
        model.addAttribute("minPriceAvailable", minPriceAvailable);
        model.addAttribute("maxPriceAvailable", maxPriceAvailable);

        model.addAttribute("selectedBrand", brandId);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedColor", color);
        model.addAttribute("selectedCity", city);
        model.addAttribute("selectedMinPrice", minPrice);
        model.addAttribute("selectedMaxPrice", maxPrice);

        return "user/cars/list";
    }
}
