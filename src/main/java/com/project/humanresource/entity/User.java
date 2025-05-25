package com.project.humanresource.entity;

import com.project.humanresource.utility.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
     Long id;
    String email;
     String password;
     boolean isActive = false;
    @Enumerated(EnumType.STRING)
     UserStatus userRole;

}
