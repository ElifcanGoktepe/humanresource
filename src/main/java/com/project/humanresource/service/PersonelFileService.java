package com.project.humanresource.service;

import com.project.humanresource.dto.request.SetPersonalFileRequestDto;
import com.project.humanresource.dto.response.PersonalFileResponseDto;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.PersonalFile;
import com.project.humanresource.entity.User;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.PersonelFileRepository;
import com.project.humanresource.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonelFileService {

    private final PersonelFileRepository personelFileRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    public void saveOrUpdateOwnPersonelFile(SetPersonalFileRequestDto dto) {
        String email= SecurityContextHolder.getContext().getAuthentication().getName();

        User user= userRepository.findByEmail(email)
                .orElseThrow(()->new HumanResourceException(ErrorType.USER_NOT_FOUND));

        Employee employee=employeeRepository.findById(user.getId())
                .orElseThrow(()->new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        PersonalFile file;

        if (employee.getPersonalFiledId()!=null) {
            file = personelFileRepository.findById(employee.getPersonalFiledId())
                    .orElseThrow(() -> new HumanResourceException(ErrorType.PERSONEL_FILE_NOT_FOUND));
        }else {
            file = new PersonalFile();
        }
        Optional.ofNullable(dto.personalPhone()).ifPresent(file::setPersonalPhone);
        Optional.ofNullable(dto.gender()).ifPresent(file::setGender);
        Optional.ofNullable(dto.birthdate()).ifPresent(file::setBirthdate);
        Optional.ofNullable(dto.nationalId()).ifPresent(file::setNationalId);
        Optional.ofNullable(dto.educationLevel()).ifPresent(file::setEducationLevel);
        Optional.ofNullable(dto.maritalStatus()).ifPresent(file::setMaritalStatus);
        Optional.ofNullable(dto.bloodType()).ifPresent(file::setBloodType);
        Optional.ofNullable(dto.numberOfChildren()).ifPresent(file::setNumberOfChildren);
        Optional.ofNullable(dto.address()).ifPresent(file::setAddress);
        Optional.ofNullable(dto.city()).ifPresent(file::setCity);
        Optional.ofNullable(dto.iban()).ifPresent(file::setIban);
        Optional.ofNullable(dto.bankName()).ifPresent(file::setBankName);
        Optional.ofNullable(dto.bankAccountNumber()).ifPresent(file::setBankAccountNumber);
        Optional.ofNullable(dto.bankAccountType()).ifPresent(file::setBankAccountType);

        personelFileRepository.save(file);

        if (employee.getPersonalFiledId() == null) {
            employee.setPersonalFiledId(file.getId());
            employeeRepository.save(employee);
        }


    }


    public PersonalFileResponseDto getPersonelFile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new HumanResourceException(ErrorType.USER_NOT_FOUND));

        Employee employee = employeeRepository.findById(user.getId())
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        if (employee.getPersonalFiledId() == null) {
            throw new HumanResourceException(ErrorType.PERSONEL_FILE_NOT_FOUND);
        }



        return Optional.ofNullable(employee.getPersonalFiledId())
                .flatMap(personelFileRepository::findById)
                .map(file -> new PersonalFileResponseDto(
                        file.getGender(),
                        file.getBirthdate(),
                        file.getPersonalPhone(),
                        file.getNationalId(),
                        file.getEducationLevel(),
                        file.getMaritalStatus(),
                        file.getBloodType(),
                        file.getNumberOfChildren(),
                        file.getAddress(),
                        file.getCity(),
                        file.getIban(),
                        file.getBankName(),
                        file.getBankAccountNumber(),
                        file.getBankAccountType()
                ))
                .orElseGet(() -> new PersonalFileResponseDto(
                        null, null, null, null, null, null,
                        null, null, null, null, null, null,
                        null, null
                ));


    }
}
