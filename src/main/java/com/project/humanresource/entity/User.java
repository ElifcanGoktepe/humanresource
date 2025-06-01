package com.project.humanresource.entity;

import com.project.humanresource.utility.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tbl_user") // 26/05 güncellendi serkan 09:40
public abstract class User extends BaseEntity {
    @Column(name="email" ,unique = true, nullable = false)
    private String email;
    @Column(name = "password")
    private String password;
    // @Enumerated(EnumType.STRING) // Removed as UserRole entity is the source of truth
    // UserStatus userRole; // Removed as UserRole entity is the source of truth
}
