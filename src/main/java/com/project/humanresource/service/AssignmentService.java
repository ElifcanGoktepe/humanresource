package com.project.humanresource.service;

import com.project.humanresource.dto.request.CreateAssignmentRequestDto;
import com.project.humanresource.dto.request.UpdateAssignmentRequestDto;
import com.project.humanresource.entity.Assignment;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public Assignment save(CreateAssignmentRequestDto dto) {
        Assignment assignment = Assignment.builder()
                .description(dto.description())
                .category(dto.category())
                .serialNumber(dto.serialNumber())
                .assignmentDate(dto.assignmentDate())
                .employeeId(dto.employeeId())
                .build();
        return assignmentRepository.save(assignment);
    }

    public Optional<Assignment> findById(Long id) {
        return assignmentRepository.findById(id);
    }

    public List<Assignment> findAll() {
        return assignmentRepository.findAll();
    }

    public List<Assignment> findByEmployeeId(Long employeeId) {
        return assignmentRepository.findByEmployeeId(employeeId);
    }

    public Assignment update(Long id, UpdateAssignmentRequestDto dto) {
        Assignment existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.ASSIGNMENT_NOT_FOUND));

        if (dto.description() != null) {
            existingAssignment.setDescription(dto.description());
        }
        if (dto.category() != null) {
            existingAssignment.setCategory(dto.category());
        }
        if (dto.serialNumber() != null) {
            existingAssignment.setSerialNumber(dto.serialNumber());
        }
        if (dto.returnDate() != null) {
            existingAssignment.setReturnDate(dto.returnDate());
        }
        // createdAt and updatedAt are handled by BaseEntity
        return assignmentRepository.save(existingAssignment);
    }

    public void delete(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new HumanResourceException(ErrorType.ASSIGNMENT_NOT_FOUND);
        }
        assignmentRepository.deleteById(id);
    }
} 