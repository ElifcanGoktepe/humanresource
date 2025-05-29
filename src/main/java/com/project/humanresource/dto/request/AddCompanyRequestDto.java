package com.project.humanresource.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddCompanyRequestDto {
    private Long id;
    private String companyName;
    private String companyAddress;
    private String companyPhoneNumber;
    private String companyEmail;
}
