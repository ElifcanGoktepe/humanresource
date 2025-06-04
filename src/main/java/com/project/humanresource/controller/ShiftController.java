package com.project.humanresource.controller;

import com.project.humanresource.dto.request.AddShiftRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.BaseEntity;
import com.project.humanresource.entity.Shift;
import com.project.humanresource.service.ShiftService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
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

    @PostMapping(ADDSHIFT)
    public ResponseEntity<BaseResponseShort<Shift>> addShift(@RequestBody AddShiftRequestDto dto){
        return ResponseEntity.ok(BaseResponseShort.<Shift>builder()
                        .message("Shift added successfully")
                        .code(200)
                        .data(shiftService.addShift(dto))
                .build());
    }

   /* @GetMapping(LISTSHIFT)
    public ResponseEntity<BaseResponseShort<List<Shift>>> getShiftList(){
        return ResponseEntity.ok(BaseResponseShort.<List<Shift>>builder()
                        .code(200)
                        .message("Shifts listed below.")
                        .data(shiftService.listAllShifts())
                .build());
    }*/

}
