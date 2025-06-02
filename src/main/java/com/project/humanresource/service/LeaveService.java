package com.project.humanresource.service;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.LeaveRequestDto;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.Leave;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.LeaveRepository;
import com.project.humanresource.utility.StateTypes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final HttpServletRequest request;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final JwtManager jwtManager;

    public Leave createLeave(LeaveRequestDto dto) {
        Long userId = jwtManager.extractUserIdFromToken(request); // ✅ token’dan çek

        if (userId == null) {
            throw new IllegalStateException("User ID not found in token.");
        }

        Long assigned = leaveRepository.getAssignedLeaveDays(userId);

        if (assigned == null || assigned == 0) {
            throw new IllegalStateException("You don't have any assigned leave days.");
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


    public void assignLeaveToEmployee(Long employeeId) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isEmpty()) {
            throw new RuntimeException("Employee not found.");
        }

        // Örnek olarak 20 izin günü veriyoruz
        List<Leave> leaves = leaveRepository.findAllByEmployeeId(employeeId);
        if (!leaves.isEmpty()) {
            // Daha önceden atanmişsa tekrar atama
            return;
        }

        Leave leave = Leave.builder()
                .leaveAssigned(20L)
                .employeeId(employeeId)
                .state(StateTypes.Approved) // Önceden atanacak izinler kullanılabilir olarak sayılır
                .build();

        leaveRepository.save(leave);
    }

}
