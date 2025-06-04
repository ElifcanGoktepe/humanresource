package com.project.humanresource.embeddable;

import com.project.humanresource.entity.Employee; // Assuming Employee entity exists for changedBy
import com.project.humanresource.utility.AssignmentStatus;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AssignmentHistoryEntry {

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_employee_id") // Keep it simple, direct employee ID
    private Employee changedBy; // Değişikliği yapan kullanıcı/çalışan

    @Enumerated(EnumType.STRING)
    private AssignmentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus newStatus;

    @Lob
    private String comment; // Yapılan işlem veya değişiklikle ilgili yorum/not

    private String action; // e.g., CREATED, UPDATED, RETURNED, STATUS_CHANGED
} 