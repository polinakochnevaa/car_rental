package com.example.car_rental.controller.admin;

import com.example.car_rental.model.Brand;
import com.example.car_rental.service.BrandService;
import com.example.car_rental.service.CarService;
import com.example.car_rental.service.ModelService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

/**
 * Контроллер администратора для управления марками автомобилей.
 * <p>
 * Предоставляет функционал для администраторов (ROLE_ADMIN):
 * <ul>
 *     <li>Просмотр списка всех марок с сортировкой</li>
 *     <li>Добавление новой марки</li>
 *     <li>Редактирование существующей марки</li>
 *     <li>Удаление марки (с проверкой на наличие связанных моделей и автомобилей)</li>
 * </ul>
 * <p>
 * Бизнес-правила при удалении:
 * <ul>
 *     <li>Марку нельзя удалить, если существуют модели этой марки</li>
 *     <li>Марку нельзя удалить, если существуют автомобили этой марки</li>
 * </ul>
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Controller
@RequestMapping("/admin/brands")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBrandController {

    /**
     * Сервис для работы с марками автомобилей.
     */
    private final BrandService brandService;

    /**
     * Сервис для работы с моделями автомобилей.
     */
    private final ModelService modelService;

    /**
     * Сервис для работы с автомобилями.
     */
    private final CarService carService;

    /**
     * Конструктор контроллера марок администратора.
     *
     * @param brandService сервис для работы с марками
     * @param modelService сервис для работы с моделями
     * @param carService   сервис для работы с автомобилями
     */
    public AdminBrandController(BrandService brandService, ModelService modelService, CarService carService) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.carService = carService;
    }

    /**
     * Отображает список всех марок автомобилей с сортировкой.
     * <p>
     * По умолчанию сортирует по имени в алфавитном порядке (без учета регистра).
     *
     * @param sortField поле для сортировки (по умолчанию "name")
     * @param sortDir   направление сортировки (asc/desc, по умолчанию "asc")
     * @param model     модель для передачи данных в представление
     * @return имя шаблона admin/brands/list
     */
    @GetMapping
    public String listBrands(
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        List<Brand> brands = brandService.getAllBrands();

        // Сортировка по имени, игнорируем регистр
        Comparator<Brand> comparator = Comparator.comparing(b -> b.getName().toLowerCase());
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }
        brands = brands.stream().sorted(comparator).toList();

        model.addAttribute("brands", brands);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        return "admin/brands/list";
    }

    /**
     * Отображает форму добавления новой марки.
     *
     * @param model модель для передачи данных в представление
     * @return имя шаблона admin/brands/add
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "admin/brands/add";
    }

    /**
     * Обрабатывает добавление новой марки.
     * <p>
     * Выполняет валидацию: название марки не может быть пустым.
     *
     * @param brand данные новой марки
     * @param model модель для передачи сообщений об ошибках
     * @return перенаправление на список марок при успехе или форму добавления при ошибке
     */
    @PostMapping("/add")
    public String addBrand(@ModelAttribute Brand brand, Model model) {
        if (brand.getName() == null || brand.getName().trim().isEmpty()) {
            model.addAttribute("error", "Название марки не может быть пустым");
            return "admin/brands/add";
        }
        brandService.saveBrand(brand);
        return "redirect:/admin/brands";
    }

    /**
     * Отображает форму редактирования существующей марки.
     *
     * @param id    идентификатор марки для редактирования
     * @param model модель для передачи данных в представление
     * @return имя шаблона admin/brands/edit или перенаправление на список марок
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Brand brand = brandService.getBrandById(id);
        if (brand == null) {
            return "redirect:/admin/brands";
        }
        model.addAttribute("brand", brand);
        return "admin/brands/edit";
    }

    /**
     * Обрабатывает редактирование марки.
     * <p>
     * Выполняет валидацию: название марки не может быть пустым.
     *
     * @param brand данные обновляемой марки
     * @param model модель для передачи сообщений об ошибках
     * @return перенаправление на список марок при успехе или форму редактирования при ошибке
     */
    @PostMapping("/edit")
    public String editBrand(@ModelAttribute Brand brand, Model model) {
        if (brand.getName() == null || brand.getName().trim().isEmpty()) {
            model.addAttribute("error", "Название марки не может быть пустым");
            return "admin/brands/edit";
        }
        brandService.saveBrand(brand);
        return "redirect:/admin/brands";
    }

    /**
     * Удаляет марку автомобиля.
     * <p>
     * Перед удалением проверяет:
     * <ul>
     *     <li>Наличие моделей этой марки</li>
     *     <li>Наличие автомобилей этой марки</li>
     * </ul>
     * Если существуют связанные модели или автомобили, удаление отклоняется.
     *
     * @param id                 идентификатор удаляемой марки
     * @param redirectAttributes атрибуты для передачи flash-сообщений
     * @return перенаправление на список марок
     */
    @PostMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Brand brand = brandService.getBrandById(id);

        if (brand == null) {
            redirectAttributes.addFlashAttribute("error", "Марка не найдена");
            return "redirect:/admin/brands";
        }

        // Проверяем, есть ли модели этой марки
        long modelsCount = modelService.countModelsByBrand(brand);
        if (modelsCount > 0) {
            redirectAttributes.addFlashAttribute("error",
                "Невозможно удалить марку \"" + brand.getName() +
                "\", так как существует " + modelsCount +
                " модел" + (modelsCount == 1 ? "ь" : (modelsCount < 5 ? "и" : "ей")) +
                " этой марки");
            return "redirect:/admin/brands";
        }

        // Проверяем, есть ли автомобили этой марки
        long carsCount = carService.countCarsByBrand(brand);
        if (carsCount > 0) {
            redirectAttributes.addFlashAttribute("error",
                "Невозможно удалить марку \"" + brand.getName() +
                "\", так как существует " + carsCount +
                " автомобил" + (carsCount == 1 ? "ь" : (carsCount < 5 ? "я" : "ей")) +
                " этой марки в автопрокате");
            return "redirect:/admin/brands";
        }

        brandService.deleteBrand(id);
        redirectAttributes.addFlashAttribute("success", "Марка \"" + brand.getName() + "\" успешно удалена");
        return "redirect:/admin/brands";
    }
}
