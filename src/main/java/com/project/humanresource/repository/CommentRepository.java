package com.project.humanresource.repository;

import com.project.humanresource.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByManagerId(Long managerId);

    Optional<Comment> findTopByManagerIdOrderByCreatedAtDesc(Long managerId);

}
