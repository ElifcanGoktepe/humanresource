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

    @PostMapping("/dev/v1/department/add")
    public ResponseEntity<BaseResponseShort<AddDepartmentRequestDto>> addDepartment(
            @RequestBody @Valid AddDepartmentRequestDto dto) {

        // Department entity'yi servis üzerinden kaydet
        Department savedDepartment = departmentService.addDepartment(dto);

        // Entity'den DTO oluştur (ID içermeyen versiyon, sadece client’a göstermek için)
        AddDepartmentRequestDto responseDto = new AddDepartmentRequestDto(
                savedDepartment.getId(),
                savedDepartment.getDepartmentName(),
                savedDepartment.getDepartmentCode(),
                savedDepartment.getCompany() != null ? savedDepartment.getCompany().getId() : null,
                savedDepartment.getCompanyBranch() != null ? savedDepartment.getCompanyBranch().getId() : null
        );

        // ResponseEntity olarak DTO'yu dön
        return ResponseEntity.ok(BaseResponseShort.<AddDepartmentRequestDto>builder()
                .data(responseDto)
                .code(200)
                .message("Department added successfully.")
                .build());
    }




 /*   @GetMapping("dev/v1/department/listAll/{id}")
    public ResponseEntity<BaseResponseShort<List<Department>>> findAllDepartments(@PathVariable Long id) {
        // id ile ilgili işlem yap (örneğin companyId'ye göre filtreleme)
        List<Department> departments = departmentService.findAllByCompanyId(id);
        return ResponseEntity.ok(BaseResponseShort.<List<Department>>builder()
                .data(departments)
                .code(200)
                .message("Departments listed successfully.")
                .build());
    }  */

    @DeleteMapping("/dev/v1/department/delete/{id}")
    public ResponseEntity<BaseResponseShort<Department>> deleteDepartment(@PathVariable Long id){
        Department deletedDepartment = departmentService.deleteDepartment(id);
        return ResponseEntity.ok(BaseResponseShort.<Department>builder()
                .data(deletedDepartment)
                .code(200)
                .message("Department deleted successfully.")
                .build());
    }


    @GetMapping("/dev/v1/department/listAllByBranchId/{id}")
    public ResponseEntity<BaseResponseShort<List<AddDepartmentRequestDto>>> getDepartmentsByBranchId(@PathVariable Long id) {
        List<AddDepartmentRequestDto> departments = departmentService.getDepartmentsByBranchId(id);
        return ResponseEntity.ok(BaseResponseShort.<List<AddDepartmentRequestDto>>builder()
                .code(200)
                .message("Departments listed successfully.")
                .data(departments)
                .build());
    }

}









