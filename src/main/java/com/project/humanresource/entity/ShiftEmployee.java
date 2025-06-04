package com.project.humanresource.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tblshiftemployee")
public class ShiftEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long shiftId;
    Long employeeId;
    LocalDateTime assignmentDate;

}
