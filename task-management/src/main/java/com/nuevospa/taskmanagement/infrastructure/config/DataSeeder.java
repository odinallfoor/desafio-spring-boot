package com.nuevospa.taskmanagement.infrastructure.config;

import com.nuevospa.taskmanagement.domain.model.status.TaskStatus;
import com.nuevospa.taskmanagement.domain.model.user.User;
import com.nuevospa.taskmanagement.infrastructure.persistence.TaskStatusRepository;
import com.nuevospa.taskmanagement.infrastructure.persistence.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initData(UserRepository userRepository,
                               TaskStatusRepository taskStatusRepository,
                               PasswordEncoder passwordEncoder){
        return args -> {

            userRepository.findByUsername("demo")
                  .orElseGet(() -> userRepository.save(
                          User.builder()
                                  .username("demo")
                                  .password(passwordEncoder.encode("demo123"))
                                  .build()
                  ));

            if (taskStatusRepository.count() == 0){
             taskStatusRepository.save(TaskStatus.builder().nombre("PENDIENTE").build());
             taskStatusRepository.save(TaskStatus.builder().nombre("EN_PROGRESO").build());
             taskStatusRepository.save(TaskStatus.builder().nombre("COMPLETADA").build());
            }
        };
    }
}
