package com.project.humanresource.repository;

import com.project.humanresource.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    // You can add custom query methods here if needed in the future
    // For example:
    // Optional<Resource> findBySerialNumber(String serialNumber);
    // List<Resource> findByType(String type);
} 