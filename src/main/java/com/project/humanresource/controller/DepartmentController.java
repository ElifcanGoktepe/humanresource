package com.project.humanresource.controller;

import com.project.humanresource.dto.request.AddDepartmentRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.Department;
import com.project.humanresource.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.humanresource.config.RestApis.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
@CrossOrigin("*")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping(ADDDERPARTAMENT)
    public ResponseEntity<BaseResponseShort<Department>> addDepartment(@RequestBody @Valid AddDepartmentRequestDto dto){
        Department savedDepartment = departmentService.addDepartment(dto);
        return ResponseEntity.ok(BaseResponseShort.<Department>builder()
                .data(savedDepartment)
                .code(200)
                .message("Department added successfully.")
                .build());
    }

    @GetMapping(FINDDEPARTMENTBYID + "/{id}")
    public ResponseEntity<BaseResponseShort<Department>> findDepartmentById(@PathVariable Long id){
        Department department = departmentService.findById(id);
        return ResponseEntity.ok(BaseResponseShort.<Department>builder()
                .data(department)
                .code(200)
                .message("Department found successfully.")
                .build());
    }

    @GetMapping(LISTALLDEPARTMENTS)
    public ResponseEntity<BaseResponseShort<List<Department>>> findAllDepartments(){
        List<Department> departments = departmentService.findAll();
        return ResponseEntity.ok(BaseResponseShort.<List<Department>>builder()
                .data(departments)
                .code(200)
                .message("Departments listed successfully.")
                .build());
    }

    @DeleteMapping(DELETEDEPARTMENTBYID + "/{id}")
    public ResponseEntity<BaseResponseShort<Department>> deleteDepartment(@PathVariable Long id){
        Department deletedDepartment = departmentService.deleteDepartment(id);
        return ResponseEntity.ok(BaseResponseShort.<Department>builder()
                .data(deletedDepartment)
                .code(200)
                .message("Department deleted successfully.")
                .build());
    }

    @GetMapping(FINDDEPARTMENTBYCODE)
    public ResponseEntity<BaseResponseShort<Department>> findByDepartmentCode(@RequestParam String code){
        Department department = departmentService.findByDepartmentCode(code);
        return ResponseEntity.ok(BaseResponseShort.<Department>builder()
                .data(department)
                .code(200)
                .message("Department found successfully.")
                .build());
    }

}









