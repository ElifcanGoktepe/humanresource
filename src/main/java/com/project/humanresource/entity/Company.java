package com.project.humanresource.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotNull @NotEmpty @NotBlank
    String companyName;
    @NotNull @NotEmpty @NotBlank
    String companyAddress;
    @NotNull @NotEmpty @NotBlank
    String companyPhoneNumber;
    @NotNull @NotEmpty @NotBlank
    String companyEmail;
    Long employerId;


}


