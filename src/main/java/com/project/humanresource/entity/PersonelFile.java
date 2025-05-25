package com.project.humanresource.entity;

import com.project.humanresource.utility.EducationLevel;
import com.project.humanresource.utility.Gender;
import com.project.humanresource.utility.MaritalStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblpersonelfile")
public class PersonelFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Gender gender;
    Date birthdate;
    @Column(length = 15)
    String personalPhone;
    @Column(length = 100)
    String personalEmail;
    @Column(length = 11,unique=true)
    String nationalId;

    EducationLevel educationLevel;
    MaritalStatus maritalStatus;
    Byte numberOfChildren;
    String address;
    String city;
    @Column(length = 26)
    String iban;
    String bankName;
    String bankAccountNumber;
    String bankAccountType;




}
