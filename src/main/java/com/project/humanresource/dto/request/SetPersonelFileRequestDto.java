package com.project.humanresource.dto.request;

import com.project.humanresource.utility.BloodType;
import com.project.humanresource.utility.EducationLevel;
import com.project.humanresource.utility.Gender;
import com.project.humanresource.utility.MaritalStatus;
import jakarta.validation.constraints.*;

import java.util.Date;


public record SetPersonelFileRequestDto(
        @NotNull Gender gender,
        @Past Date birthdate,
        @Pattern(regexp = "^\\d{11}$") String personalPhone,
        @Email String personalEmail,
        @NotBlank String nationalId,
        @NotNull EducationLevel educationLevel,
        @NotNull MaritalStatus maritalStatus,
        @NotNull BloodType bloodType,
        Byte numberOfChildren,
        String address,
        String city,
        @Size(min = 26, max = 26) String iban,
        String bankName,
        String bankAccountNumber,
        String bankAccountType
) {
}
