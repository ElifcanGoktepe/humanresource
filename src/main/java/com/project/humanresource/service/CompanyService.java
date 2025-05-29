package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddCompanyRequestDto;
import com.project.humanresource.entity.Company;

import com.project.humanresource.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;







    public List<AddCompanyRequestDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AddCompanyRequestDto getCompanyById(Long id) {
        Optional<Company> companyOpt = companyRepository.findById(id);
        return companyOpt.map(this::toDto).orElse(null);
    }

    public AddCompanyRequestDto addCompany(AddCompanyRequestDto dto) {
        Company company = toEntity(dto);
        Company saved = companyRepository.save(company);
        return toDto(saved);
    }

    public AddCompanyRequestDto updateCompany(Long id, AddCompanyRequestDto dto) {
        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            company.setCompanyName(dto.getCompanyName());
            company.setCompanyAddress(dto.getCompanyAddress());
            company.setCompanyPhoneNumber(dto.getCompanyPhoneNumber());
            company.setCompanyEmail(dto.getCompanyEmail());
            return toDto(companyRepository.save(company));
        }
        return null;
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public List<AddCompanyRequestDto> searchCompaniesByName(String name) {
        return companyRepository.findByCompanyNameContainingIgnoreCase(name).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AddCompanyRequestDto updateCompanyEmail(Long id, String email) {
        Optional<Company> companyOpt = companyRepository.findById(id);
        if (companyOpt.isPresent()) {
            Company company = companyOpt.get();
            company.setCompanyEmail(email);
            return toDto(companyRepository.save(company));
        }
        return null;
    }

    private AddCompanyRequestDto toDto(Company company) {
        return AddCompanyRequestDto.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .companyAddress(company.getCompanyAddress())
                .companyPhoneNumber(company.getCompanyPhoneNumber())
                .companyEmail(company.getCompanyEmail())
                .build();
    }

    private Company toEntity(AddCompanyRequestDto dto) {
        return Company.builder()
                .id(dto.getId())
                .companyName(dto.getCompanyName())
                .companyAddress(dto.getCompanyAddress())
                .companyPhoneNumber(dto.getCompanyPhoneNumber())
                .companyEmail(dto.getCompanyEmail())
                .build();
    }
}
