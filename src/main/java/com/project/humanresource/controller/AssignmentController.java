package com.project.humanresource.controller;

import com.project.humanresource.dto.request.AssignmentRequestDto;
import com.project.humanresource.dto.response.AssignmentResponseDto;
import com.project.humanresource.entity.Assignment;
import com.project.humanresource.service.AssignmentService;
import com.project.humanresource.dto.response.BaseResponse;
import com.project.humanresource.utility.AssignmentCategory;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    
    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<BaseResponse<Assignment>> createAssignment(@Valid @RequestBody AssignmentRequestDto dto) {
        Assignment saved = assignmentService.createAssignment(dto);
        BaseResponse<Assignment> response = new BaseResponse<>(true, "Assignment created successfully", saved);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Assignment>> updateAssignment(
            @PathVariable Long id, 
            @Valid @RequestBody AssignmentRequestDto dto) {
        try {
            Assignment updated = assignmentService.updateAssignment(id, dto);
            BaseResponse<Assignment> response = new BaseResponse<>(true, "Assignment updated successfully", updated);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new BaseResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Assignment>> getAssignmentById(@PathVariable Long id) {
        Assignment assignment = assignmentService.findById(id).orElse(null);
        if (assignment == null) {
            return ResponseEntity.ok(new BaseResponse<>(false, "Assignment not found", null));
        }
        return ResponseEntity.ok(new BaseResponse<>(true, "Assignment found", assignment));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<AssignmentResponseDto>>> getAllAssignments() {
        List<AssignmentResponseDto> assignments = assignmentService.getAllAssignmentsWithEmployeeDetails();
        BaseResponse<List<AssignmentResponseDto>> response = new BaseResponse<>(true, "Assignments retrieved successfully", assignments);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<BaseResponse<List<AssignmentResponseDto>>> getAssignmentsByEmployeeId(@PathVariable Long employeeId) {
        List<AssignmentResponseDto> assignments = assignmentService.getAssignmentsByEmployeeId(employeeId);
        BaseResponse<List<AssignmentResponseDto>> response = new BaseResponse<>(true, "Employee assignments retrieved successfully", assignments);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<BaseResponse<List<Assignment>>> getAssignmentsByCategory(@PathVariable AssignmentCategory category) {
        List<Assignment> assignments = assignmentService.findByCategory(category);
        BaseResponse<List<Assignment>> response = new BaseResponse<>(true, "Category assignments retrieved successfully", assignments);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<BaseResponse<List<AssignmentResponseDto>>> getActiveAssignments() {
        List<AssignmentResponseDto> assignments = assignmentService.getActiveAssignments();
        BaseResponse<List<AssignmentResponseDto>> response = new BaseResponse<>(true, "Active assignments retrieved successfully", assignments);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<BaseResponse<Assignment>> returnAssignment(
            @PathVariable Long id,
            @RequestParam(required = false) LocalDate returnDate) {
        try {
            LocalDate actualReturnDate = returnDate != null ? returnDate : LocalDate.now();
            Assignment returned = assignmentService.returnAssignment(id, actualReturnDate);
            BaseResponse<Assignment> response = new BaseResponse<>(true, "Assignment returned successfully", returned);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new BaseResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<String>> deleteAssignment(@PathVariable Long id) {
        try {
            assignmentService.deleteById(id);
            BaseResponse<String> response = new BaseResponse<>(true, "Assignment deleted successfully", null);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new BaseResponse<>(false, e.getMessage(), null));
        }
    }
} 