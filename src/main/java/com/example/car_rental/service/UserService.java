package com.example.car_rental.service;

import com.example.car_rental.model.User;
import com.example.car_rental.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersFiltered(String email, String role) {
        return userRepository.findAll().stream()
                .filter(u -> (email == null || email.isBlank() || (u.getEmail() != null && u.getEmail().toLowerCase().contains(email.toLowerCase()))))
                .filter(u -> (role == null || role.isBlank() || role.equals(u.getRole())))
                .toList();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public boolean existsByDriverLicenseSeriesAndNumber(String series, String number) {
        return userRepository.existsByDriverLicenseSeriesAndDriverLicenseNumber(series, number);
    }

    public boolean existsByPassportSeriesAndPassportNumber(String series, String number) {
        return userRepository.existsByPassportSeriesAndPassportNumber(series, number);
    }

    public boolean isUserDataUnique(User user) {
        if (userRepository.existsByEmail(user.getEmail())) return false;
        if (existsByPhone(user.getPhone())) return false;
        if (existsByDriverLicenseSeriesAndNumber(user.getDriverLicenseSeries(), user.getDriverLicenseNumber())) return false;
        if (existsByPassportSeriesAndPassportNumber(user.getPassportSeries(), user.getPassportNumber())) return false;
        return true;
    }

    public boolean isPasswordStrong(String password) {
        if (password == null) return false;
        if (password.length() < 8) return false;
        if (password.matches(".*(.)\\1{3,}.*")) return false;
        if (!password.matches(".*\\d.*")) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }

    public boolean isPhoneValid(String phone) {
        return phone != null && phone.matches("\\+7\\d{10}");
    }

    public boolean isDriverLicenseSeriesValid(String series) {
        return series != null && series.matches("\\d{4}");
    }

    public boolean isDriverLicenseNumberValid(String number) {
        return number != null && number.matches("\\d{6}");
    }

    public boolean isPassportSeriesValid(String series) {
        return series != null && series.matches("\\d{4}");
    }

    public boolean isPassportNumberValid(String number) {
        return number != null && number.matches("\\d{6}");
    }

    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("{bcrypt}")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

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

    public User updateUserRoleOnly(Long userId, String newRole) {
        User userFromDb = getUserById(userId);
        if (userFromDb == null) {
            return null;
        }
        userFromDb.setRole(newRole);
        return userRepository.save(userFromDb);
    }
}
