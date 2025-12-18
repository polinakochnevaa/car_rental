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

@Controller
@RequestMapping("/admin/brands")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBrandController {

    private final BrandService brandService;
    private final ModelService modelService;
    private final CarService carService;

    public AdminBrandController(BrandService brandService, ModelService modelService, CarService carService) {
        this.brandService = brandService;
        this.modelService = modelService;
        this.carService = carService;
    }

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

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "admin/brands/add";
    }

    @PostMapping("/add")
    public String addBrand(@ModelAttribute Brand brand, Model model) {
        if (brand.getName() == null || brand.getName().trim().isEmpty()) {
            model.addAttribute("error", "Название марки не может быть пустым");
            return "admin/brands/add";
        }
        brandService.saveBrand(brand);
        return "redirect:/admin/brands";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Brand brand = brandService.getBrandById(id);
        if (brand == null) {
            return "redirect:/admin/brands";
        }
        model.addAttribute("brand", brand);
        return "admin/brands/edit";
    }

    @PostMapping("/edit")
    public String editBrand(@ModelAttribute Brand brand, Model model) {
        if (brand.getName() == null || brand.getName().trim().isEmpty()) {
            model.addAttribute("error", "Название марки не может быть пустым");
            return "admin/brands/edit";
        }
        brandService.saveBrand(brand);
        return "redirect:/admin/brands";
    }

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
