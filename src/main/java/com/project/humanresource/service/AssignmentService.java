package com.project.humanresource.service;

import com.project.humanresource.dto.request.AssignmentRequestDto;
import com.project.humanresource.dto.response.AssignmentResponseDto;
import com.project.humanresource.entity.Assignment;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.repository.AssignmentRepository;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.utility.AssignmentCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    
    private final AssignmentRepository assignmentRepository;
    private final EmployeeRepository employeeRepository;

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

    public List<Assignment> findByCategory(AssignmentCategory category) {
        return assignmentRepository.findByCategory(category);
    }

    public Assignment createAssignment(AssignmentRequestDto dto) {
        Assignment assignment = Assignment.builder()
                .description(dto.description())
                .category(dto.category())
                .serialNumber(dto.serialNumber())
                .assignmentDate(dto.assignmentDate())
                .returnDate(dto.returnDate())
                .employeeId(dto.employeeId())
                .build();
        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(Long id, AssignmentRequestDto dto) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));
        
        assignment.setDescription(dto.description());
        assignment.setCategory(dto.category());
        assignment.setSerialNumber(dto.serialNumber());
        assignment.setAssignmentDate(dto.assignmentDate());
        assignment.setReturnDate(dto.returnDate());
        assignment.setEmployeeId(dto.employeeId());
        
        return assignmentRepository.save(assignment);
    }

    public Assignment returnAssignment(Long id, LocalDate returnDate) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));
        
        assignment.setReturnDate(returnDate);
        return assignmentRepository.save(assignment);
    }

    public void deleteById(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new RuntimeException("Assignment not found with id: " + id);
        }
        assignmentRepository.deleteById(id);
    }

    public List<AssignmentResponseDto> getAllAssignmentsWithEmployeeDetails() {
        List<Assignment> assignments = assignmentRepository.findAll();
        return assignments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<AssignmentResponseDto> getAssignmentsByEmployeeId(Long employeeId) {
        List<Assignment> assignments = assignmentRepository.findByEmployeeId(employeeId);
        return assignments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<AssignmentResponseDto> getActiveAssignments() {
        List<Assignment> assignments = assignmentRepository.findByReturnDateIsNull();
        return assignments.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private AssignmentResponseDto convertToResponseDto(Assignment assignment) {
        Optional<Employee> employee = employeeRepository.findById(assignment.getEmployeeId());
        
        String firstName = "";
        String lastName = "";
        
        if (employee.isPresent()) {
            firstName = employee.get().getFirstName();
            lastName = employee.get().getLastName();
        }
        
        return new AssignmentResponseDto(
                assignment.getId(),
                assignment.getDescription(),
                assignment.getCategory(),
                assignment.getSerialNumber(),
                assignment.getAssignmentDate(),
                assignment.getReturnDate(),
                assignment.getEmployeeId(),
                firstName,
                lastName,
                assignment.getReturnDate() != null
        );
    }
} 