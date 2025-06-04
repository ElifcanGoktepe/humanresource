package com.project.humanresource.entity;

import com.project.humanresource.utility.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tbl_user") // 26/05 güncellendi serkan 09:40
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="email" ,unique = true, nullable = false) // 26/05 13:34 eklendi serkan kılıçdere
    private String email;
@Column(name = "password")
    private String password;


    @Enumerated(EnumType.STRING)
     UserStatus userRole;
}
