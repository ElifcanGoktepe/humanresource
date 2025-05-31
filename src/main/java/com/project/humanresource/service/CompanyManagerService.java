package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddCommentDto;
import com.project.humanresource.dto.request.AddCompanyManagerDto;
import com.project.humanresource.dto.request.AddRoleRequestDto;
import com.project.humanresource.entity.Comment;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.entity.UserRole;
import com.project.humanresource.repository.CommentRepository;
import com.project.humanresource.repository.CompanyRepository;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.repository.UserRoleRepository;
import com.project.humanresource.utility.UserStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyManagerService {

    private final EmployeeRepository employeeRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailVerificationService emailVerificationService;
    private final CommentRepository commentRepository;

    @Transactional
    public void appliedCompanyManager(AddCompanyManagerDto dto) {

        Employee manager = Employee.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .companyName(dto.companyName())
                .titleName(dto.titleName())
                .isActivated(false)
                .isApproved(false)
                .build();

        // İlk kaydet
        manager = employeeRepository.save(manager);

        // managerId olarak kendi id'sini set et
        manager.setManagerId(manager.getId());
        employeeRepository.save(manager);

        UserRole managerRole = UserRole.builder()
                .userStatus(UserStatus.Manager)
                .userId(manager.getId())
                .build();
        userRoleRepository.save(managerRole);

        emailVerificationService.sendApprovalRequestToAdmin(manager);
    }


    public void addComment(AddCommentDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("✅ Authenticated Email: " + email);

        Employee manager = employeeRepository.findById(dto.managerId())
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        Comment comment = Comment.builder()
                .managerId(manager.getId())
                .managerName(manager.getFirstName() + " " + manager.getLastName())
                .commentText(dto.commentText())
                .photoUrl(dto.photoUrl())  // dışarıdan gelen foto url'si
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }


    @Transactional
    public Comment updateComment(Long commentId, Long managerId, AddCommentDto dto) {

        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new RuntimeException("Comment not found");
        }

        Comment comment = optionalComment.get();

        // Yorum sahibi manager mı kontrolü
        if (!comment.getManagerId().equals(managerId)) {
            throw new RuntimeException("Unauthorized to update this comment");
        }

        // Güncelleme
        comment.setCommentText(dto.commentText());
        comment.setPhotoUrl(dto.photoUrl());

        return commentRepository.save(comment);
    }



}
