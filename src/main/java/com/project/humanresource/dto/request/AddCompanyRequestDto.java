package com.project.humanresource.dto.request;

import com.project.humanresource.entity.CompanyBranch;


import java.util.List;

public record AddCompanyRequestDto(


        Long id,

        String companyName,
                String companyPhoneNumber,
                String companyAddress,
        String companyEmail,
        List<CompanyBranch> branches
) {}

