package com.example.car_rental.controller.admin;

import com.example.car_rental.model.Brand;
import com.example.car_rental.model.Car;
import com.example.car_rental.service.BrandService;
import com.example.car_rental.service.CarService;
import com.example.car_rental.service.ModelService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер администратора для управления автомобилями.
 * <p>
 * Предоставляет функционал для администраторов (ROLE_ADMIN):
 * <ul>
 *     <li>Просмотр списка всех автомобилей с многокритериальной фильтрацией и сортировкой</li>
 *     <li>Добавление нового автомобиля</li>
 *     <li>Редактирование существующего автомобиля</li>
 *     <li>Удаление автомобиля (с проверкой на статус)</li>
 * </ul>
 * <p>
 * Поддерживаемые фильтры:
 * <ul>
 *     <li>По марке</li>
 *     <li>По государственному номеру</li>
 *     <li>По городу</li>
 *     <li>По статусу (AVAILABLE, RENTED, RESERVED, MAINTENANCE)</li>
 * </ul>
 * <p>
 * Бизнес-правила при удалении:
 * <ul>
 *     <li>Автомобиль нельзя удалить, если он арендован (RENTED) или зарезервирован (RESERVED)</li>
 * </ul>
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Controller
@RequestMapping("/admin/cars")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCarController {

    /**
     * Сервис для работы с автомобилями.
     */
    private final CarService carService;

    /**
     * Сервис для работы с марками автомобилей.
     */
    private final BrandService brandService;

    /**
     * Сервис для работы с моделями автомобилей.
     */
    private final ModelService modelService;

    /**
     * Конструктор контроллера автомобилей администратора.
     *
     * @param carService   сервис для работы с автомобилями
     * @param brandService сервис для работы с марками
     * @param modelService сервис для работы с моделями
     */
    public AdminCarController(CarService carService, BrandService brandService, ModelService modelService) {
        this.carService = carService;
        this.brandService = brandService;
        this.modelService = modelService;
    }

    /**
     * Отображает список всех автомобилей с фильтрацией и сортировкой.
     * <p>
     * Поддерживает фильтрацию по марке, государственному номеру, городу и статусу.
     * Сортировка доступна по модели и цене.
     *
     * @param brandFilter  идентификатор марки для фильтрации (0 = все марки)
     * @param plate        государственный номер для фильтрации (поиск по подстроке)
     * @param cityFilter   город для фильтрации
     * @param statusFilter статус автомобиля для фильтрации
     * @param sortField    поле для сортировки (model или price)
     * @param sortDir      направление сортировки (asc/desc)
     * @param model        модель для передачи данных в представление
     * @return имя шаблона admin/cars/list
     */
    @GetMapping
    public String listCars(
            @RequestParam(required = false, defaultValue = "0") Long brandFilter,
            @RequestParam(required = false) String plate,
            @RequestParam(required = false, defaultValue = "") String cityFilter,
            @RequestParam(required = false, defaultValue = "") String statusFilter,
            @RequestParam(required = false, defaultValue = "model") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            Model model) {

        List<Car> cars = carService.getAllCars().stream()
                .filter(car -> brandFilter == 0 || (car.getBrand() != null && car.getBrand().getId().equals(brandFilter)))
                .filter(car -> plate == null || plate.isBlank() || (car.getLicensePlate() != null && car.getLicensePlate().toLowerCase().contains(plate.toLowerCase())))
                .filter(car -> cityFilter == null || cityFilter.isBlank() || (car.getCity() != null && car.getCity().equals(cityFilter)))
                .filter(car -> statusFilter == null || statusFilter.isBlank() || (car.getStatus() != null && car.getStatus().equals(statusFilter)))
                .sorted(getComparator(sortField, sortDir))
                .collect(Collectors.toList());

        // Получаем список уникальных городов
        List<String> cities = List.of("Ижевск", "Воткинск", "Сарапул", "Глазов", "Можга");

        // Получаем список статусов
        List<String> statuses = List.of("AVAILABLE", "RENTED", "RESERVED", "MAINTENANCE");

        model.addAttribute("cars", cars);
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("cities", cities);
        model.addAttribute("statuses", statuses);
        model.addAttribute("brandFilter", brandFilter);
        model.addAttribute("plate", plate);
        model.addAttribute("cityFilter", cityFilter);
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return "admin/cars/list";
    }

    /**
     * Создает компаратор для сортировки автомобилей.
     *
     * @param sortField поле для сортировки (model или price)
     * @param sortDir   направление сортировки (asc/desc)
     * @return компаратор для сортировки автомобилей
     */
    private Comparator<Car> getComparator(String sortField, String sortDir) {
        Comparator<Car> comparator = switch (sortField) {
            case "model" -> Comparator.comparing(car -> car.getModel().getName(), String.CASE_INSENSITIVE_ORDER);
            case "price" -> Comparator.comparingInt(Car::getPricePerDay);
            default -> Comparator.comparing(car -> car.getModel().getName(), String.CASE_INSENSITIVE_ORDER);
        };
        return "desc".equalsIgnoreCase(sortDir) ? comparator.reversed() : comparator;
    }

    /**
     * Отображает форму добавления нового автомобиля.
     *
     * @param model модель для передачи данных в представление
     * @return имя шаблона admin/cars/add
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("models", modelService.getAllModels());
        return "admin/cars/add";
    }

    /**
     * Обрабатывает добавление нового автомобиля.
     * <p>
     * Выполняет валидацию: государственный номер не может быть пустым.
     * Связывает автомобиль с выбранными маркой и моделью.
     *
     * @param car   данные нового автомобиля
     * @param model модель для передачи сообщений об ошибках
     * @return перенаправление на список автомобилей при успехе или форму добавления при ошибке
     */
    @PostMapping("/add")
    public String addCar(@ModelAttribute Car car, Model model) {
        if (car.getLicensePlate() == null || car.getLicensePlate().trim().isEmpty()) {
            model.addAttribute("error", "Гос. номер не может быть пустым");
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("models", modelService.getAllModels());
            return "admin/cars/add";
        }
        if (car.getBrand() != null && car.getBrand().getId() != null) {
            car.setBrand(brandService.getBrandById(car.getBrand().getId()));
        }
        if (car.getModel() != null && car.getModel().getId() != null) {
            car.setModel(modelService.getModelById(car.getModel().getId()));
        }
        carService.saveCar(car);
        return "redirect:/admin/cars";
    }

    /**
     * Отображает форму редактирования существующего автомобиля.
     *
     * @param id    идентификатор автомобиля для редактирования
     * @param model модель для передачи данных в представление
     * @return имя шаблона admin/cars/edit или перенаправление на список автомобилей
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Car car = carService.getCarById(id);
        if (car == null) {
            return "redirect:/admin/cars";
        }
        model.addAttribute("car", car);
        model.addAttribute("brands", brandService.getAllBrands());
        model.addAttribute("models", modelService.getAllModels());
        return "admin/cars/edit";
    }

    /**
     * Обрабатывает редактирование автомобиля.
     * <p>
     * Выполняет валидацию: государственный номер не может быть пустым.
     * Обновляет связи с маркой и моделью.
     *
     * @param car   данные обновляемого автомобиля
     * @param model модель для передачи сообщений об ошибках
     * @return перенаправление на список автомобилей при успехе или форму редактирования при ошибке
     */
    @PostMapping("/edit")
    public String editCar(@ModelAttribute Car car, Model model) {
        if (car.getLicensePlate() == null || car.getLicensePlate().trim().isEmpty()) {
            model.addAttribute("error", "Гос. номер не может быть пустым");
            model.addAttribute("brands", brandService.getAllBrands());
            model.addAttribute("models", modelService.getAllModels());
            return "admin/cars/edit";
        }
        if (car.getBrand() != null && car.getBrand().getId() != null) {
            car.setBrand(brandService.getBrandById(car.getBrand().getId()));
        }
        if (car.getModel() != null && car.getModel().getId() != null) {
            car.setModel(modelService.getModelById(car.getModel().getId()));
        }
        carService.saveCar(car);
        return "redirect:/admin/cars";
    }

    /**
     * Удаляет автомобиль.
     * <p>
     * Проверяет статус автомобиля перед удалением.
     * Автомобили со статусом RENTED или RESERVED не могут быть удалены.
     *
     * @param id    идентификатор удаляемого автомобиля
     * @param model модель для передачи данных в представление
     * @return перенаправление на список автомобилей
     */
    @PostMapping("/delete/{id}")
    public String deleteCar(@PathVariable Long id, Model model) {
        Car car = carService.getCarById(id);
        if (car != null && ("RENTED".equals(car.getStatus()) || "RESERVED".equals(car.getStatus()))) {
            // Нельзя удалить автомобиль, который занят или зарезервирован
            return "redirect:/admin/cars";
        }
        carService.deleteCar(id);
        return "redirect:/admin/cars";
    }
}
