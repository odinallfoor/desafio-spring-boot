package com.nuevospa.taskmanagement.infrastructure.config;

import com.nuevospa.taskmanagement.infrastructure.config.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desactivamos CSRF (estándar para REST)
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(auth -> auth
                        // 2. Permitimos acceso público a los endpoints de autenticación y Swagger
                        .requestMatchers("/api/auth/**",
                                "/h2-console/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        // 3. Todas las demás peticiones deben estar autenticadas
                        .anyRequest().authenticated()
                )
                // 4. Configuración de sesión sin estado
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 5. Agregar el proveedor de autenticación
                .authenticationProvider(authenticationProvider())
                // 6. Agregar el filtro JWT ANTES del filtro de autenticación por defecto
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Define el cifrador de contraseñas.
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        // 1. Define el codificador BCrypt
        BCryptPasswordEncoder bCryptEncoder = new BCryptPasswordEncoder();

        // 2. Crea un mapa de codificadores para la delegación
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", bCryptEncoder);

        // 3. Crea el DelegatingPasswordEncoder, indicando 'bcrypt' como ID por defecto.
        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }

    /**
     * Define el proveedor de autenticación: usa el UserDetailsService
     * y el PasswordEncoder para validar credenciales.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
