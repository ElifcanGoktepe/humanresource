package com.project.humanresource.service;

import com.project.humanresource.entity.Assignment;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;

    public Assignment save(Assignment assignment) {
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

    public Assignment update(Long id, Assignment updatedAssignmentDetails) {
        Assignment existingAssignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.ASSIGNMENT_NOT_FOUND));
        existingAssignment.setDescription(updatedAssignmentDetails.getDescription());
        existingAssignment.setCategory(updatedAssignmentDetails.getCategory());
        existingAssignment.setSerialNumber(updatedAssignmentDetails.getSerialNumber());
        existingAssignment.setAssignmentDate(updatedAssignmentDetails.getAssignmentDate());
        existingAssignment.setReturnDate(updatedAssignmentDetails.getReturnDate());
        existingAssignment.setEmployeeId(updatedAssignmentDetails.getEmployeeId());
        return assignmentRepository.save(existingAssignment);
    }

    public void delete(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new HumanResourceException(ErrorType.ASSIGNMENT_NOT_FOUND);
        }
        assignmentRepository.deleteById(id);
    }
} 