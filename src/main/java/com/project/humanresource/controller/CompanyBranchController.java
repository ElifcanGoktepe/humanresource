package com.project.humanresource.controller;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.AddCompanyBranchRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.CompanyBranch;
import com.project.humanresource.service.CompanyBranchService;
import jakarta.validation.Valid;
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
public class CompanyBranchController {

    private final CompanyBranchService companyBranchService;

    @PostMapping("/dev/v1/companybranch/add")
    public ResponseEntity<BaseResponseShort<CompanyBranch>> addCompanyBranch(@RequestBody @Valid AddCompanyBranchRequestDto dto) {
        CompanyBranch branch = companyBranchService.addCompanyBranch(dto);
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(branch)
                .code(200)
                .message("Company branch added successfully")
                .build());
    }

    @DeleteMapping("/dev/v1/companybranch/delete/{id}")
    public ResponseEntity<BaseResponseShort<CompanyBranch>> deleteCompanyBranch(@PathVariable Long id) {
        CompanyBranch deletedBranch = companyBranchService.deleteCompanyBranch(id);
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(deletedBranch)
                .code(200)
                .message("Company branch deleted successfully")
                .build());
    }

    @GetMapping("/dev/v1/companybranch/listAll/{id}")
    public ResponseEntity<BaseResponseShort<List<CompanyBranch>>> findAll() {
        List<CompanyBranch> branches = companyBranchService.findAll();
        return ResponseEntity.ok(BaseResponseShort.<List<CompanyBranch>>builder()
                .data(branches)
                .code(200)
                .message("Company branches listed successfully")
                .build());
    }



}







