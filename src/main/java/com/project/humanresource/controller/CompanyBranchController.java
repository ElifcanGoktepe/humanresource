package com.project.humanresource.controller;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.AddCompanyBranchRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.CompanyBranch;
import com.project.humanresource.service.CompanyBranchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.humanresource.config.RestApis.*;


@RestController
@RequiredArgsConstructor
@RequestMapping()
@CrossOrigin("*")
public class CompanyBranchController {
    private final CompanyBranchService companyBranchService;
    private final JwtManager jwtManager;



    @PostMapping(ADDCOMPANYBRANCH)
    public ResponseEntity<BaseResponseShort<CompanyBranch>> addCompanyBranch(@RequestBody @Valid AddCompanyBranchRequestDto dto) {
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(companyBranchService.addCompanyBranch(dto))
                .code(200)
                .message("Company Branch Added Successfully")
                .build());

    }

    @DeleteMapping(DELETECOMPANYBRANCHBYID + "/{id}")
    public ResponseEntity<BaseResponseShort<CompanyBranch>> deleteCompanyBranchById(@PathVariable Long id) {
        CompanyBranch deletedCompanyBranch = companyBranchService.deleteCompanyBranch(id);
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(deletedCompanyBranch)
                .code(200)
                .message("Company Branch Deleted Successfully")
                .build());
    }

    @DeleteMapping(DELETECOMPANYBRANCHBYCOMPANYBRANCHCODE+"/companyBranchCode")
    public ResponseEntity<BaseResponseShort<CompanyBranch>> deleteCompanyBranchByCompanyBranchCode(@RequestParam String companyBranchCode) {
        CompanyBranch deletedCompanyBranch = companyBranchService.deleteCompanyBranchByCompanyBranchCode(companyBranchCode);
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(deletedCompanyBranch)
                .code(200)
                .message("Company Branch Deleted Successfully")
                .build());
    }


    @GetMapping(LISTOFALLBRANCHESOFCOMPANY)
    public ResponseEntity<BaseResponseShort<List<CompanyBranch>>> listAllCompanyBranches() {
        List<CompanyBranch> companyBranches = companyBranchService.findAll();
        {
            return ResponseEntity.ok(BaseResponseShort.<List<CompanyBranch>>builder()
                    .data(companyBranches)
                    .code(200)
                    .message("All Company Branches Listed Successfully")
                    .build());

        }
    }

    @GetMapping(FINDCOMPANYBRANCHBYADDRESS)
    public ResponseEntity<BaseResponseShort<CompanyBranch>> findCompanyBranchByAddress(@RequestParam @Valid AddCompanyBranchRequestDto dto) {
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(companyBranchService.findByCompanyBranchAddress(dto.companyBranchAddress()))
                .code(200)
                .message("Company found successfully")
                .build());
    }

    @GetMapping(FINDCOMPANYBRANCHBYEMAILADDRESS)
    public ResponseEntity<BaseResponseShort<CompanyBranch>> findCompanyBranchByEmailAddress(@RequestParam AddCompanyBranchRequestDto dto) {
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                .data(companyBranchService.findByCompanyBranchEmailAddress(dto.companyBranchEmailAddress()))
                .code(200)
                .message("Company found successfully")
                .build());
    }

    @GetMapping(FINDCOMPANYBRANCHBYPHONENUMBER)
    public ResponseEntity<BaseResponseShort<CompanyBranch>> findCompanyBranchByPhoneNumber(@RequestParam AddCompanyBranchRequestDto dto) {
        return ResponseEntity.ok(BaseResponseShort.<CompanyBranch>builder()
                        .data(companyBranchService.findByCompanyBranchPhoneNumber(dto.companyBranchPhoneNumber()))
                        .code(200)
                        .message("Company Found Successfully")
                .build());
    }

}








