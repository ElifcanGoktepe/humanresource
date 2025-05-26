package com.project.humanresource.controller;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.AddDepartmentRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.Department;
import com.project.humanresource.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.humanresource.config.RestApis.ADDDEPARTMENT;
import static com.project.humanresource.config.RestApis.ADMIN;

@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN)
@CrossOrigin("*")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final JwtManager jwtManager;

@PostMapping(ADDDEPARTMENT)
    public ResponseEntity<BaseResponseShort<Department>> addDepartment(@RequestBody AddDepartmentRequestDto dto) {
    return ResponseEntity.ok(BaseResponseShort.<Department>builder()
                    .data(departmentService.addDepartment(dto))
                    .code(200)
                    .message("Department added successfully")
            .build());

}









}
