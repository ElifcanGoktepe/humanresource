package com.project.humanresource.dto.response;

import com.project.humanresource.utility.BloodType;
import com.project.humanresource.utility.EducationLevel;
import com.project.humanresource.utility.Gender;
import com.project.humanresource.utility.MaritalStatus;

import java.util.Date;

public record PersonalFileResponseDto(

        Gender gender,
        Date birthdate,
        String personalPhone,
        String nationalId,
        EducationLevel educationLevel,
        MaritalStatus maritalStatus,
        BloodType bloodType,
        Byte numberOfChildren,
        String address,
        String city,
        String iban,
        String bankName,
        String bankAccountNumber,
        String bankAccountType
) {
}
