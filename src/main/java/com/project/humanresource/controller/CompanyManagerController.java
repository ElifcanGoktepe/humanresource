package com.project.humanresource.controller;

import com.project.humanresource.config.JwtUser;
import com.project.humanresource.dto.request.AddCommentDto;
import com.project.humanresource.dto.request.AddCompanyManagerDto;
import com.project.humanresource.dto.request.CommentResponseDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.Comment;
import com.project.humanresource.entity.EmailVerification;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import com.project.humanresource.repository.CommentRepository;
import com.project.humanresource.repository.EmployeeRepository;
import com.project.humanresource.service.CompanyManagerService;
import com.project.humanresource.service.EmailVerificationService;
import com.project.humanresource.service.EmployeeService;
import com.project.humanresource.service.FileUploadService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.project.humanresource.config.RestApis.*;


@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
public class CompanyManagerController {

    private final CompanyManagerService companyManagerService;
    private final EmailVerificationService emailVerificationService;
    private final EmployeeService employeeService;
    private final FileUploadService fileUploadService;
    private final CommentRepository commentRepository;



    // başvuruyu employee tablosuna manager olarak kaydeder, isActivated = false, isApproved = false
    @PostMapping(REGISTER)
    public ResponseEntity<BaseResponseShort<Boolean>> appliedCompanyManager(@RequestBody AddCompanyManagerDto dto, HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        companyManagerService.appliedCompanyManager(dto, token);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                        .code(200)
                        .message("Company manager applied")
                        .data(true)
                .build());
    }
    // isApproved = true
    @GetMapping("/approve/{employeeId}")
    public ResponseEntity<String> approveManager(@PathVariable Long employeeId) {
        boolean activated = emailVerificationService.approveCompanyManager(employeeId);
        if (activated) {
            return ResponseEntity.ok("✅ Kullanıcı onaylandı ve tamamen aktif hale geldi.");
        } else {
            return ResponseEntity.ok("🟡 Kullanıcı onaylandı ama mail doğrulaması bekleniyor.");
        }
    }
    //isActivated = true
    @GetMapping("/api/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean result = emailVerificationService.verifyToken(token);
        return result ?
                ResponseEntity.ok("✅ Email activated.") :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Invalid or expired token.");
    }








    @GetMapping("/dev/v1/comments")
    public List<CommentResponseDto> getComments() {
        return companyManagerService.getAllComments();
    }

    @DeleteMapping("/dev/v1/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        companyManagerService.deleteCommentById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/dev/v1/comments/{id}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long id,
            @RequestBody AddCommentDto dto) {

        Comment updatedComment = companyManagerService.updateComment(id, dto);
        return ResponseEntity.ok(updatedComment);
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }




    @PostMapping("/dev/v1/comments/with-photo")
    public ResponseEntity<?> addCommentWithPhoto(@RequestPart("commentText") String commentText,
                                                 @RequestPart(value = "file", required = false) MultipartFile file) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        Long userId = jwtUser.getUserId();


        Employee manager = employeeService.findById(userId)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        String photoUrl = null;
        if (file != null && !file.isEmpty()) {
            String imageRelativeUrl = fileUploadService.uploadProfileImage(file, manager.getId(), manager.getProfileImageUrl());
            photoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(imageRelativeUrl)
                    .toUriString();

            // optional: çalışan kaydını da güncelle
            manager.setProfileImageUrl(imageRelativeUrl);
            employeeService.save(manager);
        }

        Comment comment = Comment.builder()
                .managerId(manager.getId())
                .managerName(manager.getFirstName() + " " + manager.getLastName())
                .commentText(commentText)
                .photoUrl(photoUrl)
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return ResponseEntity.ok(Map.of("message", "Comment saved", "data", comment));
    }




}




