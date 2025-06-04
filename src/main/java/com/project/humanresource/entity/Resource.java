package com.project.humanresource.entity;

import com.project.humanresource.utility.AssignmentCategory; // Can be used to categorize resources
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Table(name = "tblresource")
public class Resource extends BaseEntity { // Assuming you have a BaseEntity with createdAt, updatedAt

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., "Dell Latitude 7490", "iPhone 13 Pro"
    private String resourceIdentifier; // Unique identifier like Serial Number, Asset Tag, IMEI, VIN

    @Enumerated(EnumType.STRING)
    private AssignmentCategory category; // COMPUTER, PHONE, VEHICLE, FURNITURE, OTHER etc.
                                     // Consider renaming/expanding AssignmentCategory or using a new ResourceCategory enum

    private String description; // Optional: further details about the resource

    // Future enhancements: status (Available, Assigned, In Repair, Retired), purchaseDate, warrantyExpiryDate, supplier etc.
} 