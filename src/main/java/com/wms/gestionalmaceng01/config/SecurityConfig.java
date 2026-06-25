package com.wms.gestionalmaceng01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.wms.gestionalmaceng01.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String LOGIN_URL = "/login";

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
              
.requestMatchers(
    LOGIN_URL,
    "/recuperarclave",
    "/css/**",
    "/js/**",
    "/api/**",
    "/error",

    "/inventario",
    "/inventario/**",

    "/recepcion",
    "/recepcion/**",

    "/productos",
    "/productos/**",

    "/categorias",
    "/categorias/**",

    "/ubicaciones",
    "/ubicaciones/**"
).permitAll()

                .anyRequest().authenticated()
            )
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl(LOGIN_URL + "?error=true")
                .permitAll()
        
            )
            .logout(logout -> logout
                .logoutSuccessUrl(LOGIN_URL + "?logout=true")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}