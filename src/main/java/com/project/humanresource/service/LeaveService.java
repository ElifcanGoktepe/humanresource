package com.project.humanresource.service;

import com.project.humanresource.dto.request.LeaveRequestDto;
import com.project.humanresource.entity.Leave;
import com.project.humanresource.repository.LeaveRepository;
import com.project.humanresource.utility.StateTypes;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final HttpServletRequest request; // ✅ doğru şekilde request enjekte ediliyor

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
}
