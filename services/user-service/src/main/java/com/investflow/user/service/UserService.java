package com.investflow.user.service;

import com.investflow.user.dto.UpdateProfileRequest;
import com.investflow.user.dto.UserProfileResponse;
import com.investflow.user.exception.ResourceNotFoundException;
import com.investflow.user.model.Role;
import com.investflow.user.model.User;
import com.investflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for email: " + email));
        return mapToProfile(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for email: " + email));

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }

        User updated = userRepository.save(user);
        log.info("Profile updated for user: {}", email);
        return mapToProfile(updated);
    }

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToProfile)
                .collect(Collectors.toList());
    }

    private UserProfileResponse mapToProfile(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
