package com.project.humanresource.repository;

import com.project.humanresource.entity.UserRole;
import com.project.humanresource.utility.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUserStatus(UserStatus userStatus);

    List<UserRole> findByUserId(Long userId);

} 