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

    @PostMapping(ADDCOMPANYBRANCH)
    public ResponseEntity<BaseResponseShort<CompanyBranch>> addCompanyBranch(@RequestBody @Valid AddCompanyBranchRequestDto dto) {
        CompanyBranch branch = companyBranchService.addCompanyBranch(dto);
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(branch)
                .code(200)
                .message("Company branch added successfully")
                .build());
    }

    @DeleteMapping(DELETECOMPANYBRANCHBYID + "/{id}")
    public ResponseEntity<BaseResponseShort<CompanyBranch>> deleteCompanyBranch(@PathVariable Long id) {
        CompanyBranch deletedBranch = companyBranchService.deleteCompanyBranch(id);
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(deletedBranch)
                .code(200)
                .message("Company branch deleted successfully")
                .build());
    }

    @GetMapping(LISTALLCOMPANYBRANCH)
    public ResponseEntity<BaseResponseShort<List<CompanyBranch>>> findAll() {
        List<CompanyBranch> branches = companyBranchService.findAll();
        return ResponseEntity.ok(BaseResponseShort.<List<CompanyBranch>>builder()
                .data(branches)
                .code(200)
                .message("Company branches listed successfully")
                .build());
    }

    @GetMapping(FINDCOMPANYBRANCHBYADDRESS)
    public ResponseEntity<BaseResponseShort<CompanyBranch>> findByAddress(@RequestParam String address) {
        CompanyBranch branch = companyBranchService.findByCompanyBranchAddress(address);
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(branch)
                .code(200)
                .message("Company branch found successfully")
                .build());
    }

    @GetMapping(FINDCOMPANYBRANCHBYEMAILADDRESS)
    public ResponseEntity<BaseResponseShort<CompanyBranch>> findByEmail(@RequestParam String email) {
        CompanyBranch branch = companyBranchService.findByCompanyBranchEmailAddress(email);
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(branch)
                .code(200)
                .message("Company branch found successfully")
                .build());
    }

    @GetMapping(FINDCOMPANYBRANCHBYPHONENUMBER)
    public ResponseEntity<BaseResponseShort<CompanyBranch>> findByPhone(@RequestParam String phoneNumber) {
        CompanyBranch branch = companyBranchService.findByCompanyBranchPhoneNumber(phoneNumber);
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(branch)
                .code(200)
                .message("Company branch found successfully")
                .build());
    }
}







