package com.project.humanresource.controller;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.LeaveRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.Leave;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.LeaveRepository;
import com.project.humanresource.service.LeaveService;
import com.project.humanresource.utility.StateTypes;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.project.humanresource.config.RestApis.REQUESTLEAVE;

@RestController
@RequiredArgsConstructor
@RequestMapping
@CrossOrigin("*")
@SecurityRequirement(name = "bearerAuth")
public class LeaveController {

    private final LeaveService leaveService;
    private final JwtManager jwtManager;
    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    @PostMapping(REQUESTLEAVE)
    public ResponseEntity<BaseResponseShort<Leave>> requestLeave (@RequestBody LeaveRequestDto dto){
        return ResponseEntity.ok(BaseResponseShort.<Leave>builder()
                        .data(leaveService.createLeave(dto))
                        .message("Leave requested.")
                        .code(200)
                .build());
    }
    @GetMapping("/leaves/pending")
    public ResponseEntity<BaseResponseShort<List<Leave>>> getPendingLeavesForManager(HttpServletRequest request) {
        Long managerId = jwtManager.extractUserIdFromToken(request);
        List<Leave> pendingLeaves = leaveService.getPendingLeavesForManager(managerId);
        return ResponseEntity.ok(BaseResponseShort.<List<Leave>>builder()
                        .data(pendingLeaves)
                        .message("Pending leaves for manager " + managerId)
                        .code(200)
                .build());
    }

    @PostMapping("/leaves/{id}/approve")
    public ResponseEntity<BaseResponseShort<Leave>> approveLeave(@PathVariable Long id, HttpServletRequest request) {
        Long managerId = (Long) request.getAttribute("userId");
        Leave approved = leaveService.approveLeave(id, managerId);
        return ResponseEntity.ok(BaseResponseShort.<Leave>builder()
                .data(approved)
                .message("Leave approved.")
                .code(200)
                .build());
    }

    @PostMapping("/leaves/{id}/reject")
    public ResponseEntity<BaseResponseShort<Leave>> rejectLeave(@PathVariable Long id, HttpServletRequest request) {
        Long managerId = (Long) request.getAttribute("userId");
        Leave rejected = leaveService.rejectLeave(id, managerId);
        return ResponseEntity.ok(BaseResponseShort.<Leave>builder()
                .data(rejected)
                .message("Leave rejected.")
                .code(200)
                .build());
    }

    @GetMapping("/leaves/approved")
    public ResponseEntity<BaseResponseShort<List<Leave>>> getApprovedLeavesForEmployee(HttpServletRequest request) {
        Long employeeId = (Long) request.getAttribute("userId");
        List<Leave> approvedLeaves = leaveService.getApprovedLeavesForEmployee(employeeId);
        return ResponseEntity.ok(BaseResponseShort.<List<Leave>>builder()
                .data(approvedLeaves)
                .message("Approved leaves fetched.")
                .code(200)
                .build());
    }
    @PutMapping("/leaves/{id}/approve")
    public ResponseEntity<BaseResponseShort<Boolean>> approveLeave(@PathVariable Long id) {
        leaveService.updateLeaveStatus(id, StateTypes.Approved);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                .data(true)
                .message("Leave approved.")
                .code(200)
                .build());
    }

    @PutMapping("/leaves/{id}/reject")
    public ResponseEntity<BaseResponseShort<Boolean>> rejectLeave(@PathVariable Long id) {
        leaveService.updateLeaveStatus(id, StateTypes.Rejected);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                .data(true)
                .message("Leave rejected.")
                .code(200)
                .build());
    }

    @GetMapping("/employee/leave-usage")
    public ResponseEntity<BaseResponseShort<Map<String, Long>>> getLeaveUsage(HttpServletRequest request) {
        Long userId = jwtManager.extractUserIdFromToken(request);

        Long total = leaveRepository.getAssignedLeaveDays(userId);
        Long used = leaveRepository.getUsedLeaveDays(userId);
        Long remaining = total - used;

        Map<String, Long> usage = Map.of(
                "used", used,
                "total", total,
                "remaining", remaining
        );

        return ResponseEntity.ok(BaseResponseShort.<Map<String, Long>>builder()
                .data(usage)
                .code(200)
                .message("Leave usage fetched.")
                .build());
    }

    @GetMapping("/manager/active-employees")
    public ResponseEntity<BaseResponseShort<List<Employee>>> getActiveEmployees(HttpServletRequest request) {
        Long managerId = jwtManager.extractUserIdFromToken(request);
        List<Employee> employees = employeeRepository.findByManagerIdAndIsActiveTrue(managerId);
        return ResponseEntity.ok(BaseResponseShort.<List<Employee>>builder()
                .data(employees)
                .code(200)
                .message("Active employees listed.")
                .build());
    }
}
