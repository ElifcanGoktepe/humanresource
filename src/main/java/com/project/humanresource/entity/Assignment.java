package com.project.humanresource.entity;

import com.project.humanresource.utility.AssignmentCategory;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "tblassignment")
@EqualsAndHashCode(callSuper = true)
public class Assignment extends BaseEntity {
    private String description;
    private AssignmentCategory category;
    private String serialNumber;
    private LocalDate assignmentDate;
    private LocalDate returnDate;
    private Long employeeId;
}

