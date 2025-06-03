package com.project.humanresource.controller;

import com.project.humanresource.dto.request.CreateAssignmentRequestDto;
import com.project.humanresource.dto.request.UpdateAssignmentRequestDto;
import com.project.humanresource.dto.response.AssignmentResponseDto;
import com.project.humanresource.dto.response.BaseResponse;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.Assignment;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin("*") // Added for consistency if other controllers have it
public class AssignmentController {
    private final AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    // Helper method to convert Entity to DTO
    private AssignmentResponseDto toAssignmentResponseDto(Assignment assignment) {
        if (assignment == null) {
            return null;
        }
        return new AssignmentResponseDto(
                assignment.getId(),
                assignment.getDescription(),
                assignment.getCategory(),
                assignment.getSerialNumber(),
                assignment.getAssignmentDate(),
                assignment.getReturnDate(),
                assignment.getEmployeeId(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt()
        );
    }

    @PostMapping
    public ResponseEntity<BaseResponse<AssignmentResponseDto>> createAssignment(@Valid @RequestBody CreateAssignmentRequestDto dto) {
        Assignment savedAssignment = assignmentService.save(dto);
        AssignmentResponseDto responseDto = toAssignmentResponseDto(savedAssignment);
        BaseResponse<AssignmentResponseDto> response = new BaseResponse<>(true, "Assignment created successfully", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<AssignmentResponseDto>> getAssignmentById(@PathVariable(name = "id") Long id) {
        Assignment assignment = assignmentService.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.ASSIGNMENT_NOT_FOUND));
        AssignmentResponseDto responseDto = toAssignmentResponseDto(assignment);
        BaseResponse<AssignmentResponseDto> response = new BaseResponse<>(true, "Assignment retrieved successfully", responseDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<AssignmentResponseDto>>> getAllAssignments() {
        List<Assignment> assignments = assignmentService.findAll();
        List<AssignmentResponseDto> responseDtos = assignments.stream()
                .map(this::toAssignmentResponseDto)
                .collect(Collectors.toList());
        BaseResponse<List<AssignmentResponseDto>> response = new BaseResponse<>(true, "All assignments retrieved successfully", responseDtos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<BaseResponse<List<AssignmentResponseDto>>> getAssignmentsByEmployeeId(@PathVariable Long employeeId) {
        List<Assignment> assignments = assignmentService.findByEmployeeId(employeeId);
        List<AssignmentResponseDto> responseDtos = assignments.stream()
                .map(this::toAssignmentResponseDto)
                .collect(Collectors.toList());
        BaseResponse<List<AssignmentResponseDto>> response = new BaseResponse<>(true, "Assignments for employee retrieved successfully", responseDtos);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<AssignmentResponseDto>> updateAssignment(@PathVariable Long id, @Valid @RequestBody UpdateAssignmentRequestDto dto) {
        Assignment updatedAssignment = assignmentService.update(id, dto);
        AssignmentResponseDto responseDto = toAssignmentResponseDto(updatedAssignment);
        BaseResponse<AssignmentResponseDto> response = new BaseResponse<>(true, "Assignment updated successfully", responseDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponseShort<Void>> deleteAssignment(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.ok(BaseResponseShort.<Void>builder()
                .code(200)
                .message("Assignment deleted successfully.")
                .build());
    }
} 