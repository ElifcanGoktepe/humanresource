package com.project.humanresource.entity;

import com.project.humanresource.utility.LeaveTypes;
import com.project.humanresource.utility.StateTypes;
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
@Table(name = "tblleave")
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String description;
    @Enumerated(EnumType.STRING)
    LeaveTypes leaveType;
    @Enumerated(EnumType.STRING)
    StateTypes state;
    Long employeeId;
}
