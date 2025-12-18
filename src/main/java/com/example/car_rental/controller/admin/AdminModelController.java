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

@Controller
@RequestMapping("/admin/models")
@PreAuthorize("hasRole('ADMIN')")
public class AdminModelController {

    private final ModelService modelService;
    private final BrandService brandService;
    private final CarService carService;

    public AdminModelController(ModelService modelService, BrandService brandService, CarService carService) {
        this.modelService = modelService;
        this.brandService = brandService;
        this.carService = carService;
    }

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

    @GetMapping("/add")
    public String showAddForm(Model uiModel) {
        uiModel.addAttribute("model", new com.example.car_rental.model.Model());
        uiModel.addAttribute("brands", brandService.getAllBrands());
        return "admin/models/add";
    }

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
