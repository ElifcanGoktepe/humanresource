package com.project.humanresource.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CompanyTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long companyId;
    Long titleId;

}
