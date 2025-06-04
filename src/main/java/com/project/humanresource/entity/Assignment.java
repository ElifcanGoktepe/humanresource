package com.project.humanresource.entity;

import com.project.humanresource.embeddable.AssignmentHistoryEntry;
import com.project.humanresource.utility.AssignmentCategory;
import com.project.humanresource.utility.AssignmentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Table(name = "tblassignment")
public class Assignment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_employee_id")
    private Employee assignedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by_employee_id")
    private Employee assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @Enumerated(EnumType.STRING)
    private AssignmentCategory category;

    private String description;

    private LocalDate assignmentDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;

    @Lob
    private String notes;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tblassignment_history", joinColumns = @JoinColumn(name = "assignment_id"))
    @OrderBy("timestamp DESC")
    private List<AssignmentHistoryEntry> history = new ArrayList<>();

    public void addHistoryEntry(String action, AssignmentStatus previousStatus, AssignmentStatus newStatus, Employee changedBy, String comment) {
        if (this.history == null) {
            this.history = new ArrayList<>();
        }
        this.history.add(new AssignmentHistoryEntry(
                java.time.LocalDateTime.now(),
                changedBy,
                previousStatus,
                newStatus,
                comment,
                action
        ));
    }
}

