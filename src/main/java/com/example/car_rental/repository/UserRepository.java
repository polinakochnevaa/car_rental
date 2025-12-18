package com.example.car_rental.repository;

import com.example.car_rental.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Репозиторий для работы с пользователями системы.
 * <p>
 * Предоставляет методы для управления пользователями, включая поиск по email,
 * проверку уникальности контактных данных и документов (email, телефон, паспорт,
 * водительское удостоверение), а также подсчет пользователей по ролям.
 * Поддерживает спецификации для динамических запросов через JpaSpecificationExecutor.
 *
 * @author ИжДрайв
 * @version 1.0
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Находит пользователя по email.
     *
     * @param email email пользователя
     * @return пользователь с указанным email или null, если не найден
     */
    User findByEmail(String email);

    /**
     * Проверяет существование пользователя с указанным email.
     *
     * @param email email для проверки
     * @return true, если пользователь с таким email существует
     */
    boolean existsByEmail(String email);

    /**
     * Проверяет существование пользователя с указанным телефоном.
     *
     * @param phone номер телефона для проверки
     * @return true, если пользователь с таким телефоном существует
     */
    boolean existsByPhone(String phone);

    /**
     * Проверяет существование пользователя с указанными серией и номером водительского удостоверения.
     *
     * @param series серия водительского удостоверения
     * @param number номер водительского удостоверения
     * @return true, если пользователь с такими данными ВУ существует
     */
    boolean existsByDriverLicenseSeriesAndDriverLicenseNumber(String series, String number);

    /**
     * Проверяет существование пользователя с указанными серией и номером паспорта.
     *
     * @param series серия паспорта
     * @param number номер паспорта
     * @return true, если пользователь с такими паспортными данными существует
     */
    boolean existsByPassportSeriesAndPassportNumber(String series, String number);

    /**
     * Подсчитывает количество пользователей с указанной ролью.
     *
     * @param role роль пользователя (ROLE_USER или ROLE_ADMIN)
     * @return количество пользователей с данной ролью
     */
    long countByRole(String role);
}
