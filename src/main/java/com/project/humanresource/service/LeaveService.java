package com.project.humanresource.service;

import com.project.humanresource.config.JwtUserDetails;
import com.project.humanresource.dto.request.LeaveRequestDto;
import com.project.humanresource.entity.Leave;
import com.project.humanresource.repository.LeaveRepository;
import com.project.humanresource.utility.StateTypes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final LeaveRepository leaveRepository;

    public Leave createLeave(LeaveRequestDto dto) {
        if (dto.startDate().isAfter(dto.endDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails userDetails = (JwtUserDetails) auth.getPrincipal();
        Long employeeId = userDetails.getEmployeeId();

        Leave leave = Leave.builder()
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .description(dto.description())
                .leaveType(dto.leaveType())
                .state(StateTypes.Pending_Approval)
                .employeeId(1L) // şimdilik elle daya gir
                .build();
        return leaveRepository.save(leave);
    }
}
