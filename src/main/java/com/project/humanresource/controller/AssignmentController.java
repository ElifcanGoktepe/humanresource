package com.project.humanresource.controller;

import com.project.humanresource.entity.Assignment;
import com.project.humanresource.service.AssignmentService;
import com.project.humanresource.dto.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;

    @Autowired
    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Assignment>> createAssignment(@RequestBody Assignment assignment) {
        Assignment saved = assignmentService.save(assignment);
        BaseResponse<Assignment> response = new BaseResponse<>(true, "Assignment created", saved);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Assignment>> getAssignmentById(@PathVariable(name = "id") Long id) {
        Assignment assignment = assignmentService.findById(id).orElse(null);
        if (assignment == null) {
            return ResponseEntity.ok(new BaseResponse<>(false, "Assignment not found", null));
        }
        return ResponseEntity.ok(new BaseResponse<>(true, "Assignment found", assignment));
    }
} 