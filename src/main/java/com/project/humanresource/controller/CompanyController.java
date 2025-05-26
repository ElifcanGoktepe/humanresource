package com.project.humanresource.controller;

import com.project.humanresource.config.JwtManager;
import com.project.humanresource.dto.request.AddCompanyRequestDto;
import com.project.humanresource.dto.response.BaseResponseShort;

import com.project.humanresource.entity.Company;

import com.project.humanresource.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.humanresource.config.RestApis.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN)
@CrossOrigin("*")
public class CompanyController {


    private final CompanyService companyService;
    private final JwtManager jwtManager;

    @PostMapping(ADDCOMPANY)
    public ResponseEntity<BaseResponseShort<Company>> addCompany(@RequestBody @Valid AddCompanyRequestDto dto){
        return ResponseEntity.ok(BaseResponseShort.<Company>builder()
                .data(companyService.addCompany(dto))
                .code(200)
                .message("Company Added successfully.")
                .build());


    }

    @GetMapping(FINDCOMPANYBYNAME)
    public ResponseEntity<BaseResponseShort<Company>> findCompanyByName(@RequestParam @Valid AddCompanyRequestDto dto){
        return ResponseEntity.ok(BaseResponseShort.<Company>builder()
                        .data(companyService.findByCompanyName(dto.companyName()))
                        .code(200)
                .message("Company found successfully")
                .build());
    }


    @GetMapping(FINDCOMPANYBYEMAILADDRESS)
    public ResponseEntity<BaseResponseShort<Company>> findCompanyByEmailAddress(@RequestParam @Valid AddCompanyRequestDto dto){
        return ResponseEntity.ok(BaseResponseShort.<Company>builder()
                .data(companyService.findByCompanyEmailAddress(dto.companyEmail()))
                .code(200)
                .message("Company found successfully")
                .build());
    }

    @GetMapping(FINDCOMPANYBYPHONENUMBER)
    public ResponseEntity<BaseResponseShort<Company>> findCompanyByPhoneNumber(@RequestParam @Valid AddCompanyRequestDto dto){
        return ResponseEntity.ok(BaseResponseShort.<Company>builder()
                .data(companyService.findByCompanyPhoneNumber(dto.companyPhoneNumber()))
                .code(200)
                .message("Company found successfully")
                .build());
    }

    @GetMapping(LISTALLCOMPANY)
    public ResponseEntity<BaseResponseShort<List<Company>>> findAll(){
        List<Company> companies = companyService.findAll();
        return ResponseEntity.ok(BaseResponseShort.<List<Company>>builder()
                .data(companies)
                .code(200)
                .message("Companies listed successfully")
                .build());
    }

    @DeleteMapping(DELETECOMPANYBYID +"/{id}")
    public ResponseEntity<BaseResponseShort<Company>> deleteCompany(@PathVariable @Valid Long id){
        Company deletedCompany = companyService.deleteCompany(id);
        return ResponseEntity.ok(BaseResponseShort.<Company>builder()
                .data(deletedCompany)
                .code(200)
                .message("Company deleted successfully")
                .build());
    }



}
