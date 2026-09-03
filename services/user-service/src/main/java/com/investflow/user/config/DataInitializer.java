package com.investflow.user.config;

import com.investflow.user.model.Role;
import com.investflow.user.model.User;
import com.investflow.user.repository.RoleRepository;
import com.investflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("ROLE_USER")
                        .description("Standard investor user")
                        .build()));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .name("ROLE_ADMIN")
                        .description("Platform administrator")
                        .build()));

        if (!userRepository.existsByEmail("admin@investflow.com")) {
            User admin = User.builder()
                    .email("admin@investflow.com")
                    .password(passwordEncoder.encode("Admin@12345"))
                    .firstName("InvestFlow")
                    .lastName("Administrator")
                    .phone("+1-555-0199")
                    .status("ACTIVE")
                    .roles(new HashSet<>(Set.of(userRole, adminRole)))
                    .build();
            userRepository.save(admin);
            log.info("Initialized default administrator: admin@investflow.com");
        }

        if (!userRepository.existsByEmail("user@investflow.com")) {
            User user = User.builder()
                    .email("user@investflow.com")
                    .password(passwordEncoder.encode("User@12345"))
                    .firstName("Alex")
                    .lastName("Mercer")
                    .phone("+1-555-0123")
                    .status("ACTIVE")
                    .roles(new HashSet<>(Set.of(userRole)))
                    .build();
            userRepository.save(user);
            log.info("Initialized default investor user: user@investflow.com");
        }
    }
}
