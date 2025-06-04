package com.project.humanresource.service;

import com.project.humanresource.config.JwtUserDetails;
import com.project.humanresource.dto.request.AddShiftRequestDto;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.Shift;
import com.project.humanresource.entity.ShiftBreak;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.ShiftBreakRepository;
import com.project.humanresource.repository.ShiftRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShiftService {

    public final ShiftRepository shiftRepository;
    private final ShiftBreakRepository shiftBreakRepository;
    private final HttpServletRequest request;
    public Shift addShift(AddShiftRequestDto dto) {

        // ✅ JwtTokenFilter tarafından set edilen userId burada alınır
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new IllegalStateException("User ID not found in request.");
        }

        List<ShiftBreak> savedBreaks = dto.shiftBreaks().stream()
                .map(b -> shiftBreakRepository.save(ShiftBreak.builder()
                        .startTime(b.startTime())
                        .endTime(b.endTime())
                        .build()))
                .toList();

        Shift shift = Shift.builder()
                .name(dto.name())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .description(dto.description())
                .employeeIds(List.of(userId))
                .shiftBreakIds(savedBreaks.stream().map(ShiftBreak::getId).toList())
                .build();

        return shiftRepository.save(shift);
    }

    public List<Shift> listAllShifts() {
        return shiftRepository.findAll();
    }
}
