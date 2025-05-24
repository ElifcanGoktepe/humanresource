package com.project.humanresource.repository;

import com.project.humanresource.entity.Assignment;
import com.project.humanresource.utility.AssignmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByEmployeeId(Long employeeId);
    List<Assignment> findByCategory(AssignmentCategory category);
    List<Assignment> findByEmployeeIdAndReturnDateIsNull(Long employeeId);
    List<Assignment> findByReturnDateIsNull();
} 