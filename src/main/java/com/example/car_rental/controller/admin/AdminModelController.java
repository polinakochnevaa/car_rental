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
import java.util.stream.Collectors;

/**
 * Контроллер администратора для управления моделями автомобилей.
 * <p>
 * Предоставляет функционал для администраторов (ROLE_ADMIN):
 * <ul>
 *     <li>Просмотр списка всех моделей с фильтрацией по марке и сортировкой</li>
 *     <li>Добавление новой модели</li>
 *     <li>Редактирование существующей модели</li>
 *     <li>Удаление модели (с проверкой на наличие связанных автомобилей)</li>
 * </ul>
 * <p>
 * Бизнес-правила при удалении:
 * <ul>
 *     <li>Модель нельзя удалить, если существуют автомобили этой модели</li>
 * </ul>
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Controller
@RequestMapping("/admin/models")
@PreAuthorize("hasRole('ADMIN')")
public class AdminModelController {

    /**
     * Сервис для работы с моделями автомобилей.
     */
    private final ModelService modelService;

    /**
     * Сервис для работы с марками автомобилей.
     */
    private final BrandService brandService;

    /**
     * Сервис для работы с автомобилями.
     */
    private final CarService carService;

    /**
     * Конструктор контроллера моделей администратора.
     *
     * @param modelService сервис для работы с моделями
     * @param brandService сервис для работы с марками
     * @param carService   сервис для работы с автомобилями
     */
    public AdminModelController(ModelService modelService, BrandService brandService, CarService carService) {
        this.modelService = modelService;
        this.brandService = brandService;
        this.carService = carService;
    }

    /**
     * Отображает список всех моделей с фильтрацией и сортировкой.
     * <p>
     * Поддерживает фильтрацию по марке (brandFilter = 0 означает "все марки").
     * По умолчанию сортирует по имени модели в алфавитном порядке (без учета регистра).
     *
     * @param brandFilter идентификатор марки для фильтрации (0 = все марки)
     * @param sortField   поле для сортировки (по умолчанию "name")
     * @param sortDir     направление сортировки (asc/desc, по умолчанию "asc")
     * @param uiModel     модель для передачи данных в представление
     * @return имя шаблона admin/models/list
     */
    @GetMapping
    public String listModels(
            @RequestParam(required = false, defaultValue = "0") Long brandFilter,
            @RequestParam(defaultValue = "name") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model uiModel) {

        List<com.example.car_rental.model.Model> allModels = modelService.getAllModels();
        List<Brand> allBrands = brandService.getAllBrands();

        // Фильтрация по марке (если выбрана не 0)
        List<com.example.car_rental.model.Model> filtered = (brandFilter == 0)
                ? allModels
                : allModels.stream()
                .filter(m -> m.getBrand().getId().equals(brandFilter))
                .collect(Collectors.toList());

        // Сортировка по названию модели
        Comparator<com.example.car_rental.model.Model> comparator = Comparator.comparing(m -> m.getName().toLowerCase());
        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }
        filtered = filtered.stream().sorted(comparator).collect(Collectors.toList());

        uiModel.addAttribute("models", filtered);
        uiModel.addAttribute("brands", allBrands);
        uiModel.addAttribute("brandFilter", brandFilter);
        uiModel.addAttribute("sortField", sortField);
        uiModel.addAttribute("sortDir", sortDir);

        return "admin/models/list";
    }

    /**
     * Отображает форму добавления новой модели.
     *
     * @param uiModel модель для передачи данных в представление
     * @return имя шаблона admin/models/add
     */
    @GetMapping("/add")
    public String showAddForm(Model uiModel) {
        uiModel.addAttribute("model", new com.example.car_rental.model.Model());
        uiModel.addAttribute("brands", brandService.getAllBrands());
        return "admin/models/add";
    }

    /**
     * Обрабатывает добавление новой модели.
     * <p>
     * Выполняет валидацию: название модели не может быть пустым.
     *
     * @param modelEntity данные новой модели
     * @param uiModel     модель для передачи сообщений об ошибках
     * @return перенаправление на список моделей при успехе или форму добавления при ошибке
     */
    @PostMapping("/add")
    public String addModel(@ModelAttribute com.example.car_rental.model.Model modelEntity, Model uiModel) {
        if (modelEntity.getName() == null || modelEntity.getName().trim().isEmpty()) {
            uiModel.addAttribute("error", "Название модели не может быть пустым");
            uiModel.addAttribute("brands", brandService.getAllBrands());
            return "admin/models/add";
        }
        modelService.saveModel(modelEntity);
        return "redirect:/admin/models";
    }

    /**
     * Отображает форму редактирования существующей модели.
     *
     * @param id      идентификатор модели для редактирования
     * @param uiModel модель для передачи данных в представление
     * @return имя шаблона admin/models/edit или перенаправление на список моделей
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model uiModel) {
        com.example.car_rental.model.Model modelEntity = modelService.getModelById(id);
        if (modelEntity == null) {
            return "redirect:/admin/models";
        }
        uiModel.addAttribute("model", modelEntity);
        uiModel.addAttribute("brands", brandService.getAllBrands());
        return "admin/models/edit";
    }

    /**
     * Обрабатывает редактирование модели.
     * <p>
     * Выполняет валидацию: название модели не может быть пустым.
     *
     * @param modelEntity данные обновляемой модели
     * @param uiModel     модель для передачи сообщений об ошибках
     * @return перенаправление на список моделей при успехе или форму редактирования при ошибке
     */
    @PostMapping("/edit")
    public String editModel(@ModelAttribute com.example.car_rental.model.Model modelEntity, Model uiModel) {
        if (modelEntity.getName() == null || modelEntity.getName().trim().isEmpty()) {
            uiModel.addAttribute("error", "Название модели не может быть пустым");
            uiModel.addAttribute("brands", brandService.getAllBrands());
            return "admin/models/edit";
        }
        modelService.saveModel(modelEntity);
        return "redirect:/admin/models";
    }

    /**
     * Удаляет модель автомобиля.
     * <p>
     * Перед удалением проверяет наличие автомобилей этой модели.
     * Если существуют связанные автомобили, удаление отклоняется.
     *
     * @param id                 идентификатор удаляемой модели
     * @param redirectAttributes атрибуты для передачи flash-сообщений
     * @return перенаправление на список моделей
     */
    @PostMapping("/delete/{id}")
    public String deleteModel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        com.example.car_rental.model.Model modelEntity = modelService.getModelById(id);

        if (modelEntity == null) {
            redirectAttributes.addFlashAttribute("error", "Модель не найдена");
            return "redirect:/admin/models";
        }

        // Проверяем, есть ли автомобили с этой моделью
        long carsCount = carService.countCarsByModel(modelEntity);

        if (carsCount > 0) {
            redirectAttributes.addFlashAttribute("error",
                "Невозможно удалить модель \"" + modelEntity.getName() +
                "\", так как существует " + carsCount +
                " автомобил" + (carsCount == 1 ? "ь" : (carsCount < 5 ? "я" : "ей")) +
                " этой модели в автопрокате");
            return "redirect:/admin/models";
        }

        modelService.deleteModel(id);
        redirectAttributes.addFlashAttribute("success", "Модель \"" + modelEntity.getName() + "\" успешно удалена");
        return "redirect:/admin/models";
    }
}
