package com.project.humanresource.service;

import com.project.humanresource.dto.request.LeaveRequestDto;
import com.project.humanresource.entity.Leave;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.LeaveRepository;
import com.project.humanresource.utility.StateTypes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final HttpServletRequest request;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;

    public Leave createLeave(LeaveRequestDto dto) {
        if (dto.startDate().isAfter(dto.endDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        // ✅ JwtTokenFilter tarafından set edilen userId burada alınır
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User ID not found in request.");
        }

        Leave leave = Leave.builder()
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .description(dto.description())
                .leaveType(dto.leaveType())
                .state(StateTypes.Pending_Approval)
                .employeeId(userId) // ✅ userId burada kullanılır
                .build();

        return leaveRepository.save(leave);
    }

    public List<Leave> getPendingLeavesForManager(Long managerId) {
        List<Long> employeeIds = employeeRepository.findEmployeeIdsByManagerId(managerId);
        return leaveRepository.findByEmployeeIdInAndState(employeeIds, StateTypes.Pending_Approval);

    }
    public Leave approveLeave(Long leaveId, Long managerId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found."));

        // Bu leave'e ait employee gerçekten bu manager'a mı bağlı?
        if (!isEmployeeOfManager(leave.getEmployeeId(), managerId)) {
            throw new RuntimeException("Unauthorized. This employee is not under your management.");
        }

        leave.setState(StateTypes.Approved);
        return leaveRepository.save(leave);
    }

    public Leave rejectLeave(Long leaveId, Long managerId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found."));

        if (!isEmployeeOfManager(leave.getEmployeeId(), managerId)) {
            throw new RuntimeException("Unauthorized. This employee is not under your management.");
        }

        leave.setState(StateTypes.Rejected);
        return leaveRepository.save(leave);
    }

    // Yardımcı method:
    private boolean isEmployeeOfManager(Long employeeId, Long managerId) {
        // Bu method EmployeeService'den yararlanarak ilgili çalışan bu manager’a bağlı mı kontrol eder.
        return employeeService.findById(employeeId)
                .map(emp -> emp.getManagerId() != null && emp.getManagerId().equals(managerId))
                .orElse(false);
    }
    public void updateLeaveState(Long leaveId, StateTypes newState) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        leave.setState(newState);
        leaveRepository.save(leave);
    }

    public List<Leave> getApprovedLeavesForEmployee(Long employeeId) {
        return leaveRepository.findAllByEmployeeIdAndState(employeeId, StateTypes.Approved);
    }
    public void updateLeaveStatus(Long leaveId, StateTypes status) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave not found"));
        leave.setState(status);
        leaveRepository.save(leave);
    }
}
