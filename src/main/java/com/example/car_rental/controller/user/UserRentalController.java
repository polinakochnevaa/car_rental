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

/**
 * Контроллер для управления арендами пользователя.
 * <p>
 * Предоставляет функционал для пользователей с ролью ROLE_USER:
 * <ul>
 *     <li>Просмотр списка своих аренд</li>
 *     <li>Создание новой аренды (бронирование автомобиля)</li>
 *     <li>Отмена аренды</li>
 *     <li>Подтверждение оплаты аренды</li>
 * </ul>
 * <p>
 * Бизнес-правила при создании аренды:
 * <ul>
 *     <li>Дата начала аренды должна быть строго завтра (не сегодня)</li>
 *     <li>Дата окончания должна быть позже даты начала</li>
 *     <li>Автомобиль должен быть доступен (статус AVAILABLE)</li>
 *     <li>При создании аренды автомобиль переводится в статус RESERVED</li>
 *     <li>После создания пользователь перенаправляется на страницу оплаты</li>
 * </ul>
 * <p>
 * Общая стоимость рассчитывается автоматически: цена за день × количество дней.
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Controller
@RequestMapping("/user/rentals")
public class UserRentalController {

    /**
     * Сервис для работы с арендами.
     */
    private final RentalService rentalService;

    /**
     * Сервис для работы с автомобилями.
     */
    private final CarService carService;

    /**
     * Конструктор контроллера аренд пользователя.
     *
     * @param rentalService сервис для работы с арендами
     * @param carService    сервис для работы с автомобилями
     */
    public UserRentalController(RentalService rentalService, CarService carService) {
        this.rentalService = rentalService;
        this.carService = carService;
    }

    /**
     * Отображает список аренд текущего пользователя.
     *
     * @param model       модель для передачи данных в представление
     * @param userDetails данные аутентифицированного пользователя
     * @return имя шаблона user/rentals/my
     */
    @GetMapping("/my")
    public String listMyRentals(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        var rentals = rentalService.getRentalsByClientEmail(userDetails.getUsername());
        model.addAttribute("rentals", rentals);
        return "user/rentals/my";
    }

    /**
     * Отображает форму создания новой аренды для выбранного автомобиля.
     * <p>
     * Проверяет доступность автомобиля (статус должен быть AVAILABLE).
     *
     * @param carId идентификатор автомобиля для аренды
     * @param model модель для передачи данных в представление
     * @return имя шаблона user/rentals/add или перенаправление на список автомобилей
     */
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

    /**
     * Обрабатывает создание новой аренды.
     * <p>
     * Выполняет валидацию:
     * <ul>
     *     <li>Дата начала должна быть строго завтра (не сегодня)</li>
     *     <li>Автомобиль должен быть доступен</li>
     *     <li>Дата окончания должна быть позже даты начала</li>
     * </ul>
     * При успешном создании переводит автомобиль в статус RESERVED
     * и перенаправляет на страницу оплаты.
     *
     * @param rental      данные создаваемой аренды
     * @param userDetails данные аутентифицированного пользователя
     * @param model       модель для передачи сообщений об ошибках
     * @return перенаправление на страницу оплаты или форму создания аренды при ошибке
     */
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

    /**
     * Отменяет аренду пользователя.
     *
     * @param rentalId    идентификатор отменяемой аренды
     * @param userDetails данные аутентифицированного пользователя
     * @return перенаправление на список аренд пользователя
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/cancel/{rentalId}")
    public String cancelRental(@PathVariable Long rentalId, @AuthenticationPrincipal UserDetails userDetails) {
        rentalService.cancelRental(rentalId);
        return "redirect:/user/rentals/my";
    }

    /**
     * Подтверждает оплату аренды.
     * <p>
     * Переводит аренду в статус PAID и автомобиль в статус RENTED.
     *
     * @param rentalId    идентификатор оплачиваемой аренды
     * @param userDetails данные аутентифицированного пользователя
     * @return перенаправление на список аренд пользователя
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/pay/{rentalId}")
    public String payRental(@PathVariable Long rentalId, @AuthenticationPrincipal UserDetails userDetails) {
        rentalService.confirmPayment(rentalId);
        return "redirect:/user/rentals/my";
    }
}
