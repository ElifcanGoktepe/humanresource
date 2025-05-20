package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddUserRequestDto;
import com.project.humanresource.dto.request.LoginRequestDto;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.User;
import com.project.humanresource.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User createUser(@Valid AddUserRequestDto dto) {
        // Önce bir User ID oluştur (örneğin 1L)
        Long userId = 1L;
        
        Employee employee = Employee.builder()
                .email(dto.email())
                .password(dto.password())
                .isActive(true)
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .companyId(dto.companyId())
                .titleId(dto.titleId())
                .personalFiledId(dto.personalFiledId())
                .userId(userId) // userId değerini ayarla
                .build();

        return userRepository.save(employee);
    }

    public Optional<User> findByEmailPassword(@Valid LoginRequestDto dto) {
        return userRepository.findOptionalByEmailAndPassword(dto.email(), dto.password());
    }
}