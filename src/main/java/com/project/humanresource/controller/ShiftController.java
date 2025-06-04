package com.project.humanresource.controller;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.AddShiftRequestDto;
import com.project.humanresource.dto.request.EmployeeWithShiftDto;
import com.project.humanresource.dto.request.UpdateShiftRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.dto.response.ShiftResponseDto;
import com.project.humanresource.entity.BaseEntity;
import com.project.humanresource.entity.Shift;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.service.ShiftService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.humanresource.config.RestApis.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@CrossOrigin("*")
@SecurityRequirement(name = "bearerAuth")
public class ShiftController {

    private final ShiftService shiftService;
    private final JwtManager jwtManager;

    @PostMapping(ADDSHIFT)
    public ResponseEntity<BaseResponseShort<Shift>> addShift(@RequestBody AddShiftRequestDto dto){
        return ResponseEntity.ok(BaseResponseShort.<Shift>builder()
                        .message("Shift added successfully")
                        .code(200)
                        .data(shiftService.addShift(dto))
                .build());
    }


    @GetMapping(LIST_SHIFT)
    public ResponseEntity<BaseResponseShort<List<Shift>>> getShiftList(){
        return ResponseEntity.ok(BaseResponseShort.<List<Shift>>builder()
                        .code(200)
                        .message("Shifts listed below.")
                        .data(shiftService.listAllShifts())
                .build());
    }

    @PutMapping(UPDATE_SHIFT)
    public ResponseEntity<BaseResponseShort<Shift>> updateShift(@RequestBody UpdateShiftRequestDto dto) {
        return ResponseEntity.ok(BaseResponseShort.<Shift>builder()
                .code(200)
                .message("Shift updated successfully")
                .data(shiftService.updateShift(dto))
                .build());
    }

    @GetMapping("/api/v1/employees/with-shifts")
    public ResponseEntity<List<EmployeeWithShiftDto>> getEmployeesWithShifts(HttpServletRequest request) {
        return ResponseEntity.ok(shiftService.getEmployeesWithShifts(request));
    }

    @DeleteMapping("/delete-shift/{id}")
    public ResponseEntity<BaseResponseShort<String>> deleteShift(@PathVariable Long id) {
        shiftService.deleteShift(id);  // servis katmanı üzerinden silme işlemi
        return ResponseEntity.ok(BaseResponseShort.<String>builder()
                .code(200)
                .message("Shift deleted successfully")
                .data("Shift with ID " + id + " has been deleted.")
                .build());
    }
    @GetMapping("/shifts-this-week")
    public ResponseEntity<List<ShiftResponseDto>> getThisWeeksShifts() {
        try {
            List<ShiftResponseDto> shifts = shiftService.getThisWeeksShifts();
            return ResponseEntity.ok(shifts);
        } catch (HumanResourceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Sabit hata tipi
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Genel hata tipi
        }
    }

}
