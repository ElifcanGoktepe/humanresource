package com.project.humanresource.controller;

import com.project.humanresource.dto.request.LeaveRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.dto.response.LeaveResponseDto;
import com.project.humanresource.entity.Leave;
import com.project.humanresource.service.LeaveService;
import com.project.humanresource.utility.StateTypes;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.humanresource.config.RestApis.REQUESTLEAVE;

@RestController
@RequiredArgsConstructor
@RequestMapping()
@CrossOrigin(origins = "http://localhost:5173")
@SecurityRequirement(name = "bearerAuth")
public class LeaveController {

    private final LeaveService leaveService;

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
        Long managerId = (Long) request.getAttribute("userId");
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


}
