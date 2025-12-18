package com.example.car_rental.service;

import com.example.car_rental.model.User;
import com.example.car_rental.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Реализация UserDetailsService для Spring Security.
 * <p>
 * Загружает детали пользователя по email (используется как username) из базы данных
 * для процесса аутентификации Spring Security. Преобразует пользователя системы
 * в формат Spring Security UserDetails с установленными ролями.
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Репозиторий для работы с пользователями
     */
    private final UserRepository userRepository;

    /**
     * Конструктор сервиса деталей пользователя.
     *
     * @param userRepository репозиторий пользователей
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Загружает пользователя по email (используется как username).
     * Преобразует пользователя из базы данных в объект UserDetails для Spring Security.
     *
     * @param email email пользователя (используется как username)
     * @return объект UserDetails с данными пользователя
     * @throws UsernameNotFoundException если пользователь с указанным email не найден
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().replace("ROLE_", ""))
                .build();
    }
}
