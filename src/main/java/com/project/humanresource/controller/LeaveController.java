package com.project.humanresource.controller;

import com.project.humanresource.dto.request.LeaveRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.Leave;
import com.project.humanresource.service.LeaveService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.humanresource.config.RestApis.REQUESTLEAVE;

@RestController
@RequiredArgsConstructor
@RequestMapping
@CrossOrigin("*")
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
}
