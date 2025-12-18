package com.example.car_rental.service;

import com.example.car_rental.model.Brand;
import com.example.car_rental.model.Model;
import com.example.car_rental.repository.ModelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelService {

    private final ModelRepository modelRepository;

    public ModelService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }

    public Model getModelById(Long id) {
        return modelRepository.findById(id).orElse(null);
    }

    public List<Model> getModelsByBrandId(Long brandId) {
        if (brandId == null) {
            return List.of();
        }
        return modelRepository.findByBrandId(brandId);
    }

    public Model saveModel(Model model) {
        return modelRepository.save(model);
    }

    public void deleteModel(Long id) {
        modelRepository.deleteById(id);
    }

    public long countModelsByBrand(Brand brand) {
        return modelRepository.countByBrand(brand);
    }
}
