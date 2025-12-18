package com.example.car_rental.service;

import com.example.car_rental.model.Brand;
import com.example.car_rental.model.Model;
import com.example.car_rental.repository.ModelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления моделями автомобилей.
 * <p>
 * Предоставляет бизнес-логику для работы с моделями автомобилей,
 * включая операции получения, создания, обновления и удаления.
 * Поддерживает каскадную загрузку моделей по марке (например,
 * при выборе марки в форме отображаются только соответствующие модели).
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Service
public class ModelService {

    /**
     * Репозиторий для работы с моделями автомобилей
     */
    private final ModelRepository modelRepository;

    /**
     * Конструктор сервиса моделей автомобилей.
     *
     * @param modelRepository репозиторий моделей
     */
    public ModelService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    /**
     * Возвращает список всех моделей автомобилей.
     *
     * @return список всех моделей
     */
    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }

    /**
     * Находит модель автомобиля по ID.
     *
     * @param id ID модели
     * @return объект модели или null, если не найдена
     */
    public Model getModelById(Long id) {
        return modelRepository.findById(id).orElse(null);
    }

    /**
     * Возвращает список моделей для указанной марки автомобиля.
     * Используется для каскадной загрузки моделей при выборе марки.
     *
     * @param brandId ID марки автомобиля
     * @return список моделей указанной марки или пустой список, если brandId null
     */
    public List<Model> getModelsByBrandId(Long brandId) {
        if (brandId == null) {
            return List.of();
        }
        return modelRepository.findByBrandId(brandId);
    }

    /**
     * Сохраняет модель автомобиля (создание или обновление).
     *
     * @param model объект модели для сохранения
     * @return сохраненная модель
     */
    public Model saveModel(Model model) {
        return modelRepository.save(model);
    }

    /**
     * Удаляет модель автомобиля по ID.
     *
     * @param id ID модели для удаления
     */
    public void deleteModel(Long id) {
        modelRepository.deleteById(id);
    }

    /**
     * Подсчитывает количество моделей для указанной марки.
     *
     * @param brand объект марки
     * @return количество моделей данной марки
     */
    public long countModelsByBrand(Brand brand) {
        return modelRepository.countByBrand(brand);
    }
}
