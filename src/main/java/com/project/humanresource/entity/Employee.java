package com.project.humanresource.entity;

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

    @Column(nullable = false,length = 50)
    String firstName;

    @Column(nullable = false,length = 50)
    String lastName;

    @Column(length = 100)
    String emailWork;

    @Column(length = 11)
    String phoneWork;

    @Column(nullable = false)
    Long companyId;

    @Column(nullable = false)
    Long titleId;

    @Column(nullable = false)
    Long personalFiledId;

    @Column(nullable = false)
    Long userId;


}
