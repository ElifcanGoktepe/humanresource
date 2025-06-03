package com.project.humanresource.service;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.AddShiftRequestDto;
import com.project.humanresource.dto.request.EmployeeWithShiftDto;
import com.project.humanresource.dto.request.UpdateShiftRequestDto;
import com.project.humanresource.dto.response.ShiftResponseDto;
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
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftService {

    public final ShiftRepository shiftRepository;
    private final ShiftBreakRepository shiftBreakRepository;
    private final HttpServletRequest request;
    private final JwtManager jwtManager;
    private final EmployeeRepository employeeRepository;

    public Shift addShift(AddShiftRequestDto dto) {
        Long userId = jwtManager.extractUserIdFromToken(request);
        if (userId == null) throw new IllegalStateException("User ID not found in token.");

        Employee manager = employeeRepository.findById(userId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        List<ShiftBreak> savedBreaks = dto.shiftBreaks().stream()
                .map(b -> {
                    if (dto.isRecurring()) {
                        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.parse(b.startTime()));
                        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.parse(b.endTime()));
                        return shiftBreakRepository.save(ShiftBreak.builder()
                                .startTime(start)
                                .endTime(end)
                                .build());
                    } else {
                        return shiftBreakRepository.save(ShiftBreak.builder()
                                .startTime(LocalDateTime.parse(b.startTime()))
                                .endTime(LocalDateTime.parse(b.endTime()))
                                .build());
                    }
                })
                .toList();

        LocalDateTime startDateTime = dto.isRecurring()
                ? LocalDateTime.of(LocalDate.now(), LocalTime.parse(dto.startTime()))
                : LocalDateTime.parse(dto.startTime());

        LocalDateTime endDateTime = dto.isRecurring()
                ? LocalDateTime.of(LocalDate.now(), LocalTime.parse(dto.endTime()))
                : LocalDateTime.parse(dto.endTime());

        Shift shift = Shift.builder()
                .name(dto.name())
                .startTime(startDateTime)
                .endTime(endDateTime)
                .description(dto.description())
                .companyId(manager.getCompanyId())
                .managerId(manager.getId())
                .isRecurring(dto.isRecurring())
                .daysOfWeek(dto.daysOfWeek() != null ? new ArrayList<>(dto.daysOfWeek()) : new ArrayList<>())
                .employeeIds(new ArrayList<>())
                .shiftBreakIds(savedBreaks.stream().map(ShiftBreak::getId).toList())
                .build();

        return shiftRepository.save(shift);
    }

    public List<Shift> listAllShifts() {
        Long userId = jwtManager.extractUserIdFromToken(request);
        if (userId == null) throw new IllegalStateException("User ID not found in token.");

        Employee manager = employeeRepository.findById(userId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        return shiftRepository.findByCompanyId(manager.getCompanyId());
    }

    public Shift updateShift(UpdateShiftRequestDto dto) {
        Long userId = jwtManager.extractUserIdFromToken(request);
        if (userId == null) throw new IllegalStateException("User ID not found in token.");

        Shift existingShift = shiftRepository.findById(dto.shiftId())
                .orElseThrow(() -> new HumanResourceException(ErrorType.SHIFT_NOT_FOUND));

        Employee manager = employeeRepository.findById(userId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        if (!existingShift.getManagerId().equals(manager.getId())) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED_ACCESS);
        }

        List<ShiftBreak> newBreaks = dto.shiftBreaks().stream()
                .map(b -> shiftBreakRepository.save(ShiftBreak.builder()
                        .startTime(b.startTime())
                        .endTime(b.endTime())
                        .build()))
                .toList();

        existingShift.setName(dto.name());
        existingShift.setStartTime(dto.startTime());
        existingShift.setEndTime(dto.endTime());
        existingShift.setDescription(dto.description());
        existingShift.setIsRecurring(dto.isRecurring());
        existingShift.setDaysOfWeek(dto.daysOfWeek() != null ? new ArrayList<>(dto.daysOfWeek()) : new ArrayList<>());
        existingShift.setShiftBreakIds(newBreaks.stream().map(ShiftBreak::getId).toList());

        return shiftRepository.save(existingShift);
    }

    public List<EmployeeWithShiftDto> getEmployeesWithShifts(HttpServletRequest request) {
        Long managerId = jwtManager.extractUserIdFromToken(request);
        if (managerId == null) throw new IllegalStateException("Manager ID not found in token.");

        List<Object[]> results = employeeRepository.findEmployeesWithShifts(managerId);

        return results.stream().map(row -> EmployeeWithShiftDto.builder()
                        .firstName((String) row[0])
                        .lastName((String) row[1])
                        .shiftStart(row[2].toString())
                        .shiftEnd(row[3].toString())
                        .build())
                .toList();
    }

    public void deleteShift(Long shiftId) {
        Long userId = jwtManager.extractUserIdFromToken(request);
        if (userId == null) throw new IllegalStateException("User ID not found in token.");

        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.SHIFT_NOT_FOUND));

        if (!shift.getManagerId().equals(userId)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED_ACCESS);
        }

        if (shift.getShiftBreakIds() != null && !shift.getShiftBreakIds().isEmpty()) {
            shiftBreakRepository.deleteAllById(shift.getShiftBreakIds());
        }

        shiftRepository.deleteById(shiftId);
    }

    public List<ShiftResponseDto> getThisWeeksShifts() {
        Long userId = jwtManager.extractUserIdFromToken(request);
        if (userId == null)
            throw new IllegalStateException("User ID not found in token.");

        Employee manager = employeeRepository.findById(userId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = endOfWeek.atTime(LocalTime.MAX);

        List<Shift> shifts = shiftRepository.findAllByCompanyIdAndStartTimeBetween(
                manager.getCompanyId(), start, end);

        return shifts.stream()
                .map(shift -> new ShiftResponseDto(
                        shift.getId(),
                        shift.getName(),
                        shift.getStartTime(),
                        shift.getEndTime(),
                        shift.getDescription(),
                        shift.getIsRecurring(),
                        shift.getDaysOfWeek(),
                        shift.getEmployeeIds(),
                        shift.getShiftBreakIds()
                ))
                .toList();
    }
}
