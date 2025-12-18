package com.example.car_rental.service;

import com.example.car_rental.model.User;
import com.example.car_rental.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления пользователями системы.
 * <p>
 * Предоставляет бизнес-логику для работы с пользователями, включая:
 * <ul>
 *     <li>Регистрацию и аутентификацию пользователей с хешированием паролей</li>
 *     <li>Валидацию данных (телефон, паспорт, водительское удостоверение)</li>
 *     <li>Проверку уникальности пользовательских данных</li>
 *     <li>Управление ролями (ROLE_USER, ROLE_ADMIN)</li>
 *     <li>Фильтрацию и поиск пользователей</li>
 * </ul>
 * <p>
 * Пароли хранятся в зашифрованном виде с использованием BCrypt.
 * Требования к паролю: минимум 8 символов, содержит цифру, заглавную букву и спецсимвол.
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Service
public class UserService {

    /**
     * Репозиторий для работы с пользователями
     */
    private final UserRepository userRepository;

    /**
     * Кодировщик паролей (BCrypt)
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор сервиса пользователей.
     *
     * @param userRepository репозиторий пользователей
     * @param passwordEncoder кодировщик паролей
     */
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Возвращает список всех пользователей системы.
     *
     * @return список всех пользователей
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Возвращает отфильтрованный список пользователей по email и роли.
     * Поиск по email регистронезависим и использует частичное совпадение.
     *
     * @param email фильтр по email (частичное совпадение), null или пустая строка - без фильтрации
     * @param role фильтр по роли (точное совпадение), null или пустая строка - без фильтрации
     * @return отфильтрованный список пользователей
     */
    public List<User> getUsersFiltered(String email, String role) {
        return userRepository.findAll().stream()
                .filter(u -> (email == null || email.isBlank() || (u.getEmail() != null && u.getEmail().toLowerCase().contains(email.toLowerCase()))))
                .filter(u -> (role == null || role.isBlank() || role.equals(u.getRole())))
                .toList();
    }

    /**
     * Находит пользователя по ID.
     *
     * @param id ID пользователя
     * @return объект пользователя или null, если не найден
     */
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Находит пользователя по email.
     *
     * @param email email пользователя
     * @return объект пользователя или null, если не найден
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Удаляет пользователя по ID.
     *
     * @param id ID пользователя для удаления
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Проверяет существование пользователя с указанным телефоном.
     *
     * @param phone номер телефона для проверки
     * @return true, если пользователь с таким телефоном существует
     */
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    /**
     * Проверяет существование пользователя с указанными серией и номером водительского удостоверения.
     *
     * @param series серия водительского удостоверения
     * @param number номер водительского удостоверения
     * @return true, если пользователь с такими данными ВУ существует
     */
    public boolean existsByDriverLicenseSeriesAndNumber(String series, String number) {
        return userRepository.existsByDriverLicenseSeriesAndDriverLicenseNumber(series, number);
    }

    /**
     * Проверяет существование пользователя с указанными серией и номером паспорта.
     *
     * @param series серия паспорта
     * @param number номер паспорта
     * @return true, если пользователь с такими паспортными данными существует
     */
    public boolean existsByPassportSeriesAndPassportNumber(String series, String number) {
        return userRepository.existsByPassportSeriesAndPassportNumber(series, number);
    }

    /**
     * Проверяет уникальность данных пользователя (email, телефон, паспорт, ВУ).
     * Используется при регистрации для проверки, что такой пользователь еще не зарегистрирован.
     *
     * @param user пользователь для проверки
     * @return true, если все данные уникальны, false - если хотя бы одно значение уже существует
     */
    public boolean isUserDataUnique(User user) {
        if (userRepository.existsByEmail(user.getEmail())) return false;
        if (existsByPhone(user.getPhone())) return false;
        if (existsByDriverLicenseSeriesAndNumber(user.getDriverLicenseSeries(), user.getDriverLicenseNumber())) return false;
        if (existsByPassportSeriesAndPassportNumber(user.getPassportSeries(), user.getPassportNumber())) return false;
        return true;
    }

    /**
     * Проверяет надежность пароля.
     * Требования: минимум 8 символов, содержит цифру, заглавную букву,
     * спецсимвол и не имеет 4+ одинаковых символов подряд.
     *
     * @param password пароль для проверки
     * @return true, если пароль соответствует требованиям надежности
     */
    public boolean isPasswordStrong(String password) {
        if (password == null) return false;
        if (password.length() < 8) return false;
        if (password.matches(".*(.)\\1{3,}.*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }

    /**
     * Проверяет корректность формата телефона (+7 и 10 цифр).
     *
     * @param phone номер телефона для проверки
     * @return true, если телефон соответствует формату +7XXXXXXXXXX
     */
    public boolean isPhoneValid(String phone) {
        return phone != null && phone.matches("\\+7\\d{10}");
    }

    /**
     * Проверяет корректность серии водительского удостоверения (4 цифры).
     *
     * @param series серия ВУ для проверки
     * @return true, если серия состоит из 4 цифр
     */
    public boolean isDriverLicenseSeriesValid(String series) {
        return series != null && series.matches("\\d{4}");
    }

    /**
     * Проверяет корректность номера водительского удостоверения (6 цифр).
     *
     * @param number номер ВУ для проверки
     * @return true, если номер состоит из 6 цифр
     */
    public boolean isDriverLicenseNumberValid(String number) {
        return number != null && number.matches("\\d{6}");
    }

    /**
     * Проверяет корректность серии паспорта (4 цифры).
     *
     * @param series серия паспорта для проверки
     * @return true, если серия состоит из 4 цифр
     */
    public boolean isPassportSeriesValid(String series) {
        return series != null && series.matches("\\d{4}");
    }

    /**
     * Проверяет корректность номера паспорта (6 цифр).
     *
     * @param number номер паспорта для проверки
     * @return true, если номер состоит из 6 цифр
     */
    public boolean isPassportNumberValid(String number) {
        return number != null && number.matches("\\d{6}");
    }

    /**
     * Сохраняет пользователя с автоматическим хешированием пароля.
     * Если пароль еще не захеширован (не начинается с "{bcrypt}"), то хеширует его.
     *
     * @param user пользователь для сохранения
     * @return сохраненный пользователь
     */
    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("{bcrypt}")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    /**
     * Обновляет данные пользователя без изменения пароля.
     * Используется при редактировании профиля или данных пользователя администратором.
     *
     * @param userFromForm пользователь с обновленными данными из формы
     * @return обновленный пользователь или null, если пользователь не найден
     */
    public User updateUserWithoutChangingPassword(User userFromForm) {
        User userFromDb = getUserById(userFromForm.getId());
        if (userFromDb == null) {
            return null;
        }
        userFromDb.setEmail(userFromForm.getEmail());
        userFromDb.setPhone(userFromForm.getPhone());
        userFromDb.setLastName(userFromForm.getLastName());
        userFromDb.setFirstName(userFromForm.getFirstName());
        userFromDb.setMiddleName(userFromForm.getMiddleName());
        userFromDb.setDriverLicenseSeries(userFromForm.getDriverLicenseSeries());
        userFromDb.setDriverLicenseNumber(userFromForm.getDriverLicenseNumber());
        userFromDb.setPassportSeries(userFromForm.getPassportSeries());
        userFromDb.setPassportNumber(userFromForm.getPassportNumber());
        userFromDb.setRole(userFromForm.getRole());
        return userRepository.save(userFromDb);
    }

    /**
     * Обновляет только роль пользователя.
     * Используется администратором для изменения прав доступа пользователя.
     *
     * @param userId ID пользователя
     * @param newRole новая роль (ROLE_USER или ROLE_ADMIN)
     * @return обновленный пользователь или null, если пользователь не найден
     */
    public User updateUserRoleOnly(Long userId, String newRole) {
        User userFromDb = getUserById(userId);
        if (userFromDb == null) {
            return null;
        }
        userFromDb.setRole(newRole);
        return userRepository.save(userFromDb);
    }
}
