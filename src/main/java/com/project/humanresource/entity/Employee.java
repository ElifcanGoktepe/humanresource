package com.project.humanresource.entity;

import com.project.humanresource.utility.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tblemployee")
public class Employee extends User {

    String firstName;
    String lastName;
    String phoneNumber;
    String companyName;
    String titleName;
    Long titleId;
    Long personalFiledId;
    Long managerId;

    @Column(nullable = false)   // 26/05 09:49 serkan güncellendi
    @Builder.Default
    boolean isActive = false; // 26/05 09:49 serkan güncellendi
    @Builder.Default
    boolean isApproved = false; // Site admin onayı
    @Builder.Default
    boolean isActivated = false; // Email doğrulaması

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
}
