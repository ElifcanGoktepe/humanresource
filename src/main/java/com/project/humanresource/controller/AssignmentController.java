package com.project.humanresource.controller;

import com.project.humanresource.dto.request.CreateAssignmentRequestDto;
import com.project.humanresource.dto.request.UpdateAssignmentRequestDto;
import com.project.humanresource.dto.response.*;
import com.project.humanresource.entity.Assignment;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.Resource;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin("*")
public class AssignmentController {
    private final AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    private AssignmentResponseDto toAssignmentResponseDto(Assignment assignment) {
        if (assignment == null) {
            return null;
        }

        Employee assignedToEntity = assignment.getAssignedTo();
        EmployeeBasicInfoDto assignedToDto = null;
        if (assignedToEntity != null) {
            assignedToDto = new EmployeeBasicInfoDto(
                    assignedToEntity.getId(),
                    assignedToEntity.getFirstName(),
                    assignedToEntity.getLastName(),
                    assignedToEntity.getEmail()
            );
        }

        Employee assignedByEntity = assignment.getAssignedBy();
        EmployeeBasicInfoDto assignedByDto = null;
        if (assignedByEntity != null) {
            assignedByDto = new EmployeeBasicInfoDto(
                    assignedByEntity.getId(),
                    assignedByEntity.getFirstName(),
                    assignedByEntity.getLastName(),
                    assignedByEntity.getEmail()
            );
        }

        Resource resourceEntity = assignment.getResource();
        ResourceBasicInfoDto resourceDto = null;
        if (resourceEntity != null) {
            resourceDto = new ResourceBasicInfoDto(
                    resourceEntity.getId(),
                    resourceEntity.getName(),
                    resourceEntity.getResourceIdentifier(),
                    resourceEntity.getCategory()
            );
        }

        List<AssignmentHistoryEntryDto> historyDtos = Collections.emptyList();
        if (assignment.getHistory() != null && !assignment.getHistory().isEmpty()) {
            historyDtos = assignment.getHistory().stream()
                    .map(entry -> {
                        EmployeeBasicInfoDto changedByDto = null;
                        if (entry.getChangedBy() != null) {
                            changedByDto = new EmployeeBasicInfoDto(
                                    entry.getChangedBy().getId(),
                                    entry.getChangedBy().getFirstName(),
                                    entry.getChangedBy().getLastName(),
                                    entry.getChangedBy().getEmail()
                            );
                        }
                        return new AssignmentHistoryEntryDto(
                                entry.getTimestamp(),
                                changedByDto,
                                entry.getAction(),
                                entry.getPreviousStatus(),
                                entry.getNewStatus(),
                                entry.getComment()
                        );
                    })
                    .collect(Collectors.toList());
        }

        return new AssignmentResponseDto(
                assignment.getId(),
                resourceDto,
                assignedToDto,
                assignedByDto,
                assignment.getStatus() != null ? assignment.getStatus().name() : null,
                assignment.getCategory() != null ? assignment.getCategory().name() : null,
                assignment.getDescription(),
                assignment.getAssignmentDate(),
                assignment.getExpectedReturnDate(),
                assignment.getActualReturnDate(),
                assignment.getNotes(),
                historyDtos,
                assignment.getCreatedAt(),
                assignment.getUpdatedAt()
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<BaseResponse<AssignmentResponseDto>> createAssignment(@Valid @RequestBody CreateAssignmentRequestDto dto) {
        Assignment savedAssignment = assignmentService.save(dto);
        AssignmentResponseDto responseDto = toAssignmentResponseDto(savedAssignment);
        BaseResponse<AssignmentResponseDto> response = new BaseResponse<>(true, "Assignment created successfully", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<BaseResponse<AssignmentResponseDto>> getAssignmentById(@PathVariable(name = "id") Long id) {
        Assignment assignment = assignmentService.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.ASSIGNMENT_NOT_FOUND));
        AssignmentResponseDto responseDto = toAssignmentResponseDto(assignment);
        BaseResponse<AssignmentResponseDto> response = new BaseResponse<>(true, "Assignment retrieved successfully", responseDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<BaseResponse<List<AssignmentResponseDto>>> getAllAssignments() {
        List<Assignment> assignments = assignmentService.findAll();
        List<AssignmentResponseDto> responseDtos = assignments.stream()
                .map(this::toAssignmentResponseDto)
                .collect(Collectors.toList());
        BaseResponse<List<AssignmentResponseDto>> response = new BaseResponse<>(true, "All assignments retrieved successfully", responseDtos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER') or (hasAuthority('EMPLOYEE') and #employeeId == authentication.principal.id)")
    public ResponseEntity<BaseResponse<List<AssignmentResponseDto>>> getAssignmentsByEmployeeId(@PathVariable Long employeeId) {
        List<Assignment> assignments = assignmentService.findByEmployeeId(employeeId);
        List<AssignmentResponseDto> responseDtos = assignments.stream()
                .map(this::toAssignmentResponseDto)
                .collect(Collectors.toList());
        BaseResponse<List<AssignmentResponseDto>> response = new BaseResponse<>(true, "Assignments for employee retrieved successfully", responseDtos);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<BaseResponse<AssignmentResponseDto>> updateAssignment(@PathVariable Long id, @Valid @RequestBody UpdateAssignmentRequestDto dto) {
        Assignment updatedAssignment = assignmentService.update(id, dto);
        AssignmentResponseDto responseDto = toAssignmentResponseDto(updatedAssignment);
        BaseResponse<AssignmentResponseDto> response = new BaseResponse<>(true, "Assignment updated successfully", responseDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    public ResponseEntity<BaseResponseShort<Void>> deleteAssignment(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.ok(BaseResponseShort.<Void>builder()
                .code(200)
                .message("Assignment deleted successfully.")
                .build());
    }
} 