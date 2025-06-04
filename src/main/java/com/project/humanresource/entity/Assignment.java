package com.project.humanresource.entity;

import com.project.humanresource.utility.AssignmentCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tblassignment")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String description;
    private AssignmentCategory category;
    private String serialNumber;
    private LocalDate assignmentDate;
    private LocalDate returnDate;
    private Long employeeId;
}

