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
    String emailWork;
    String phoneWork;
    String companyName;
    String titleName;
    Long companyId;
    Long titleId;
    Long personalFiledId;
    Long managerId;

    boolean isApproved = false; // Site admin onayı
    boolean isActivated = false; // Email doğrulaması

}
