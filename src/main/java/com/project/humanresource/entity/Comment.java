package com.project.humanresource.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long managerId;

    private String managerName;

    @Column(length = 1000)
    private String commentText;

    private String photoUrl; // Manager fotoğrafı veya company logo URL'si

    private LocalDateTime createdAt;
}
