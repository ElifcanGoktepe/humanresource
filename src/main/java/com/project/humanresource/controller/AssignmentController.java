package com.project.humanresource.controller;

import com.project.humanresource.entity.Assignment;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.service.AssignmentService;
import com.project.humanresource.dto.response.BaseResponse;
import com.project.humanresource.exception.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<BaseResponse<Assignment>> createAssignment(@RequestBody Assignment assignment) {
        if (assignment.getEmployeeId() == null) {
            throw new HumanResourceException(ErrorType.BADREQUEST, "Employee ID is required for an assignment.");
        }
        if (assignment.getDescription() == null || assignment.getDescription().trim().isEmpty()) {
            throw new HumanResourceException(ErrorType.BADREQUEST, "Description is required for an assignment.");
        }
        if (assignment.getCategory() == null) {
            throw new HumanResourceException(ErrorType.BADREQUEST, "Category is required for an assignment.");
        }
        if (assignment.getAssignmentDate() == null) {
            throw new HumanResourceException(ErrorType.BADREQUEST, "Assignment date is required for an assignment.");
        }
        Assignment saved = assignmentService.save(assignment);
        BaseResponse<Assignment> response = new BaseResponse<>(true, "Assignment created successfully.", saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Assignment>> getAssignmentById(@PathVariable Long id) {
        Assignment assignment = assignmentService.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.ASSIGNMENT_NOT_FOUND));
        return ResponseEntity.ok(new BaseResponse<>(true, "Assignment retrieved successfully.", assignment));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<Assignment>>> getAllAssignments() {
        List<Assignment> assignments = assignmentService.findAll();
        if (assignments.isEmpty()) {
            return ResponseEntity.ok(new BaseResponse<>(true, "No assignments found.", assignments));
        }
        return ResponseEntity.ok(new BaseResponse<>(true, "Assignments retrieved successfully.", assignments));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<BaseResponse<List<Assignment>>> getAssignmentsByEmployeeId(@PathVariable Long employeeId) {
        List<Assignment> assignments = assignmentService.findByEmployeeId(employeeId);
        if (assignments.isEmpty()) {
            return ResponseEntity.ok(new BaseResponse<>(true, "No assignments found for this employee.", assignments));
        }
        return ResponseEntity.ok(new BaseResponse<>(true, "Assignments retrieved successfully for employee.", assignments));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Assignment>> updateAssignment(@PathVariable Long id, @RequestBody Assignment assignmentDetails) {
        if (assignmentDetails.getEmployeeId() == null) {
             throw new HumanResourceException(ErrorType.BADREQUEST, "Employee ID is required for an assignment.");
        }
        if (assignmentDetails.getDescription() == null || assignmentDetails.getDescription().trim().isEmpty()) {
            throw new HumanResourceException(ErrorType.BADREQUEST, "Description is required for an assignment.");
        }
        if (assignmentDetails.getCategory() == null) {
            throw new HumanResourceException(ErrorType.BADREQUEST, "Category is required for an assignment.");
        }
        if (assignmentDetails.getAssignmentDate() == null) {
            throw new HumanResourceException(ErrorType.BADREQUEST, "Assignment date is required for an assignment.");
        }
        Assignment updatedAssignment = assignmentService.update(id, assignmentDetails);
        return ResponseEntity.ok(new BaseResponse<>(true, "Assignment updated successfully.", updatedAssignment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteAssignment(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.ok(new BaseResponse<>(true, "Assignment deleted successfully.", null));
    }
} 