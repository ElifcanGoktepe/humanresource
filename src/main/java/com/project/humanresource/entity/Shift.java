package com.project.humanresource.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tblshift")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    private Long companyId;

    private Long managerId;

    private Boolean isRecurring; // true -> haftalık tekrar, false -> tek seferlik

    @ElementCollection
    @CollectionTable(name = "shift_days_of_week", joinColumns = @JoinColumn(name = "shift_id"))
    @Column(name = "day_of_week")
    private List<Integer> daysOfWeek; // 1 = Pazartesi ... 7 = Pazar
    public void setDaysOfWeek(List<Integer> daysOfWeek) {
        this.daysOfWeek = new ArrayList<>(daysOfWeek);
    }

    @ElementCollection
    @CollectionTable(name = "shift_employee_ids", joinColumns = @JoinColumn(name = "shift_id"))
    @Column(name = "employee_id")
    private List<Long> employeeIds;

    @ElementCollection
    @CollectionTable(name = "shift_break_ids", joinColumns = @JoinColumn(name = "shift_id"))
    @Column(name = "break_id")
    private List<Long> shiftBreakIds;







}
