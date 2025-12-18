package com.example.car_rental.config;

import com.example.car_rental.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности Spring Security для системы аренды автомобилей.
 * <p>
 * Настраивает:
 * <ul>
 *     <li>Правила доступа к URL на основе ролей (ROLE_USER, ROLE_ADMIN)</li>
 *     <li>Форму входа и выхода из системы</li>
 *     <li>Кодирование паролей с использованием BCrypt</li>
 *     <li>Провайдер аутентификации на основе UserDetailsService</li>
 * </ul>
 * <p>
 * Структура доступа:
 * <ul>
 *     <li>Публичные URL: главная страница, регистрация, вход, статические ресурсы</li>
 *     <li>/user/** - доступ только для пользователей с ролью USER</li>
 *     <li>/admin/** - доступ только для администраторов с ролью ADMIN</li>
 *     <li>Все остальные URL требуют аутентификации</li>
 * </ul>
 *
 * @author ИжДрайв
 * @version 1.0
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Сервис для загрузки деталей пользователя при аутентификации
     */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Конструктор конфигурации безопасности.
     *
     * @param userDetailsService сервис для работы с пользовательскими данными
     */
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Создает и настраивает цепочку фильтров безопасности.
     * Определяет правила доступа к URL, конфигурацию входа/выхода и отключает CSRF защиту.
     *
     * @param http объект конфигурации HTTP Security
     * @return настроенная цепочка фильтров безопасности
     * @throws Exception если произошла ошибка конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Публичные URL
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // URL для пользователей с ролью USER
                        .requestMatchers("/user/**").hasRole("USER")

                        // Административные URL с ролью ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Все остальные запросы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Создает кодировщик паролей BCrypt.
     * Используется для хеширования паролей при регистрации и проверки при входе.
     *
     * @return кодировщик паролей BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создает провайдер аутентификации на основе DAO.
     * Связывает UserDetailsService и PasswordEncoder для процесса аутентификации.
     *
     * @return настроенный провайдер аутентификации
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
