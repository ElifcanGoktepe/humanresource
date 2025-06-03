package com.project.humanresource.service;

import com.project.humanresource.dto.request.AddCommentDto;
import com.project.humanresource.dto.request.AddCompanyManagerDto;
import com.project.humanresource.config.JwtUser;
import com.project.humanresource.dto.response.CommentResponseDto;
import com.project.humanresource.entity.Comment;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.CommentRepository;
import com.project.humanresource.repository.EmployeeRepository;
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
    private final EmailVerificationService emailVerificationService;
    private final CommentRepository commentRepository;

    @Transactional
    public void appliedCompanyManager(AddCompanyManagerDto dto, String token) {
        if (employeeRepository.findByEmail(dto.email()).isPresent()) {
            throw new HumanResourceException(ErrorType.EMAIL_ALREADY_EXISTS);
        }

        Employee manager = Employee.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .companyName(dto.companyName())
                .titleName(dto.titleName())
                .userRole(UserStatus.Manager)
                .isActive(false)
                .isApproved(false)
                .isActivated(false)
                .build();

        manager = employeeRepository.save(manager);

        manager.setManagerId(manager.getId());
        employeeRepository.save(manager);

        emailVerificationService.sendApprovalRequestToAdmin(manager, token);
    }

    public void addComment(AddCommentDto dto) {
        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Employee manager = employeeRepository.findById(dto.managerId())
                .orElseThrow(() -> new HumanResourceException(ErrorType.MANAGER_NOT_FOUND, "Manager to be commented on not found"));

        Comment comment = Comment.builder()
                .managerId(manager.getId())
                .managerName(manager.getFirstName() + " " + manager.getLastName())
                .commentText(dto.commentText())
                .photoUrl(dto.photoUrl())
                .createdAt(LocalDateTime.now())
                .commenterId(jwtUser.getUserId())
                .commenterName(jwtUser.getFirstName() + " " + jwtUser.getLastName())
                .build();

        commentRepository.save(comment);
    }

    public List<CommentResponseDto> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(comment -> new CommentResponseDto(
                        comment.getId(),
                        comment.getManagerId(),
                        comment.getManagerName(),
                        comment.getCommentText(),
                        comment.getPhotoUrl(),
                        comment.getCreatedAt(),
                        comment.getCommenterId(),
                        comment.getCommenterName()
                ))
                .toList();
    }

    public void deleteCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new HumanResourceException(ErrorType.COMMENT_NOT_FOUND));

        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = jwtUser.getUserId();
        boolean isAdmin = jwtUser.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("Admin") || auth.getAuthority().equals("ROLE_Admin"));

        if (!comment.getCommenterId().equals(currentUserId) && !isAdmin) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "You are not authorized to delete this comment.");
        }

        commentRepository.deleteById(id);
    }

    @Transactional
    public Comment updateComment(Long commentId, AddCommentDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.COMMENT_NOT_FOUND));

        JwtUser jwtUser = (JwtUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = jwtUser.getUserId();

        if (!comment.getCommenterId().equals(currentUserId)) {
            throw new HumanResourceException(ErrorType.UNAUTHORIZED, "You are not authorized to update this comment.");
        }

        comment.setCommentText(dto.commentText());
        comment.setPhotoUrl(dto.photoUrl());

        return commentRepository.save(comment);
    }
}
