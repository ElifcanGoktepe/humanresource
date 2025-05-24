package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddRoleRequestDto;
import com.project.humanresource.entity.UserRole;
import com.project.humanresource.repository.UserRepository;
import com.project.humanresource.repository.UserRoleRepository;
import com.project.humanresource.utility.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;

    public Optional<UserRole> findByUserStatus(UserStatus userStatus) {
        return userRoleRepository.findByUserStatus(userStatus);
    }

    public List<UserRole> findAllRole(Long userId){
        return userRoleRepository.findByUserId(userId);
    }

    public UserRole createUserRole(AddRoleRequestDto dto) {
        UserRole userRole = UserRole.builder()
                .userStatus(dto.userStatus())
                .userId(dto.userId())
                .build();
        return userRoleRepository.save(userRole);
    }

    public boolean hasRole(Long userId,UserStatus userStatus) {
        return userRoleRepository.existsByUserIdAndUserStatus(userId, userStatus);
    }
}