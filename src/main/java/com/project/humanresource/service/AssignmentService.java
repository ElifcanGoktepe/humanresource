package com.project.humanresource.service;

import com.project.humanresource.config.JwtUser;
import com.project.humanresource.dto.request.CreateAssignmentRequestDto;
import com.project.humanresource.dto.request.UpdateAssignmentRequestDto;
import com.project.humanresource.entity.Assignment;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.Resource;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.AssignmentRepository;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.ResourceRepository;
import com.project.humanresource.utility.AssignmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final ResourceRepository resourceRepository;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository,
                             EmployeeRepository employeeRepository,
                             ResourceRepository resourceRepository) {
        this.assignmentRepository = assignmentRepository;
        this.employeeRepository = employeeRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional
    public Assignment save(CreateAssignmentRequestDto dto) {
        Resource resource = resourceRepository.findById(dto.resourceId())
                .orElseThrow(() -> new HumanResourceException(ErrorType.RESOURCE_NOT_FOUND));

        Employee assignedToEmployee = employeeRepository.findById(dto.assignedToEmployeeId())
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND, "Employee to be assigned to not found."));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long authenticatedUserId;
        if (principal instanceof JwtUser) {
            authenticatedUserId = ((JwtUser) principal).getUserId();
        } else {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "User details not found in security context.");
        }
        Employee assignedByEmployee = employeeRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND, "Assigning employee (logged in user) not found."));

        if (dto.expectedReturnDate().isBefore(dto.assignmentDate())) {
            throw new HumanResourceException(ErrorType.INVALID_RETURN_DATE);
        }

        Assignment assignment = Assignment.builder()
                .resource(resource)
                .assignedTo(assignedToEmployee)
                .assignedBy(assignedByEmployee)
                .assignmentDate(dto.assignmentDate())
                .expectedReturnDate(dto.expectedReturnDate())
                .description(dto.description())
                .category(dto.category())
                .notes(dto.notes())
                .status(AssignmentStatus.ACTIVE)
                .actualReturnDate(null)
                .history(new ArrayList<>())
                .build();

        assignment.addHistoryEntry(
                "ASSIGNMENT_CREATED",
                null,
                assignment.getStatus(),
                assignedByEmployee,
                "New assignment created."
        );

        return assignmentRepository.save(assignment);
    }

    public Optional<Assignment> findById(Long id) {
        return assignmentRepository.findById(id);
    }

    public List<Assignment> findAll() {
        return assignmentRepository.findAll();
    }

    public List<Assignment> findByEmployeeId(Long assignedToEmployeeId) {
        return assignmentRepository.findByAssignedTo_Id(assignedToEmployeeId);
    }

    @Transactional
    public Assignment update(Long id, UpdateAssignmentRequestDto dto) {
        Assignment existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.ASSIGNMENT_NOT_FOUND));
        
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long authenticatedUserId;
        if (principal instanceof JwtUser) {
            authenticatedUserId = ((JwtUser) principal).getUserId();
        } else {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "User details not found for update operation.");
        }
        Employee changedByEmployee = employeeRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND, "Updating employee (logged in user) not found."));

        AssignmentStatus previousStatus = existingAssignment.getStatus();
        boolean updated = false;
        StringBuilder commentBuilder = new StringBuilder("Assignment updated: ");

        if (dto.description() != null && !dto.description().equals(existingAssignment.getDescription())) {
            existingAssignment.setDescription(dto.description());
            commentBuilder.append("Description changed. ");
            updated = true;
        }
        if (dto.category() != null && !dto.category().equals(existingAssignment.getCategory())) {
            existingAssignment.setCategory(dto.category());
            commentBuilder.append("Category changed. ");
            updated = true;
        }
        if (dto.expectedReturnDate() != null && !dto.expectedReturnDate().equals(existingAssignment.getExpectedReturnDate())) {
            if (dto.expectedReturnDate().isBefore(existingAssignment.getAssignmentDate())) {
                throw new HumanResourceException(ErrorType.INVALID_RETURN_DATE, "New expected return date cannot be before the assignment date.");
            }
            existingAssignment.setExpectedReturnDate(dto.expectedReturnDate());
            commentBuilder.append("Expected return date changed. ");
            updated = true;
        }
        if (dto.notes() != null && !dto.notes().equals(existingAssignment.getNotes())) {
            existingAssignment.setNotes(dto.notes());
            commentBuilder.append("Notes changed. ");
            updated = true;
        }
        if (dto.status() != null && !dto.status().equals(existingAssignment.getStatus())) {
            // TODO: Add logic here to validate status transitions if needed
            existingAssignment.setStatus(dto.status());
            commentBuilder.append("Status changed from ").append(previousStatus).append(" to ").append(dto.status()).append(". ");
            updated = true;
        }
        // Handle actualReturnDate update
        if (dto.actualReturnDate() != null && !dto.actualReturnDate().equals(existingAssignment.getActualReturnDate())) {
            existingAssignment.setActualReturnDate(dto.actualReturnDate());
            commentBuilder.append("Actual return date updated. ");
             // If an actual return date is set, consider changing status to RETURNED or COMPLETED
            if (existingAssignment.getStatus() == AssignmentStatus.ACTIVE) { // Example condition
                 existingAssignment.setStatus(AssignmentStatus.RETURNED);
                 commentBuilder.append("Status automatically changed to RETURNED. ");
            }
            updated = true;
        }

        if (updated) {
            existingAssignment.addHistoryEntry(
                "ASSIGNMENT_UPDATED",
                previousStatus,
                existingAssignment.getStatus(),
                changedByEmployee,
                commentBuilder.toString().trim()
            );
            return assignmentRepository.save(existingAssignment);
        }
        return existingAssignment;
    }

    @Transactional
    public void delete(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
            .orElseThrow(() -> new HumanResourceException(ErrorType.ASSIGNMENT_NOT_FOUND));
        
        // Optional: Add a history entry before deleting or change status to DELETED/CANCELLED
        // Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Long authenticatedUserId = ((JwtUser) principal).getUserId(); // Simplified, add error handling
        // Employee deletedByEmployee = employeeRepository.findById(authenticatedUserId).orElse(null); 
        // assignment.addHistoryEntry("ASSIGNMENT_DELETED", assignment.getStatus(), null, deletedByEmployee, "Assignment record deleted.");
        // assignmentRepository.save(assignment); // If changing status instead of physical delete

        assignmentRepository.deleteById(id);
    }
} 