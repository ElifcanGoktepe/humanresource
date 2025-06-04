package com.project.humanresource.controller;

import com.project.humanresource.dto.request.AddCommentDto;
import com.project.humanresource.dto.request.AddCompanyManagerDto;
import com.project.humanresource.dto.request.CommentResponseDto;
import com.project.humanresource.dto.response.BaseResponseShort;
import com.project.humanresource.entity.Comment;
import com.project.humanresource.entity.EmailVerification;
import com.project.humanresource.entity.Employee;
import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
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





    @PostMapping("/dev/v1/addcomment")
    public ResponseEntity<BaseResponseShort<Boolean>> addComment(@RequestBody AddCommentDto dto, Authentication authentication) {
        Long managerId = dto.managerId();

        if (managerId == null) {
            // Authentication'dan giriş yapan kullanıcının employee id'sini çek
            Employee currentUser = employeeService.getCurrentEmployee(authentication);
            managerId = currentUser.getId();
        }

        // Yeni DTO ile managerId set et (immutable record olduğu için yeni nesne oluştur)
        AddCommentDto updatedDto = new AddCommentDto(managerId, dto.commentText(), dto.photoUrl());

        companyManagerService.addComment(updatedDto);
        return ResponseEntity.ok(BaseResponseShort.<Boolean>builder()
                .code(200)
                .message("Comment added successfully")
                .data(true)
                .build());
    }


    @GetMapping("/comments")
    public List<CommentResponseDto> getComments() {
        return companyManagerService.getAllComments();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        companyManagerService.deleteCommentById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/comment/{id}")
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


    @PostMapping("/{id}/upload-profile")
    public ResponseEntity<?> uploadEmployeeProfileImage(@PathVariable Long id,
                                                        @RequestParam("file") MultipartFile file) {
        Employee employee = employeeService.findById(id)
                .orElseThrow(() -> new HumanResourceException(ErrorType.EMPLOYEE_NOT_FOUND));

        // Eski resmi silip yeniyi yükle
        String imageRelativeUrl = fileUploadService.uploadProfileImage(file, id, employee.getProfileImageUrl());

        // Tam URL'yi oluştur
        String fullUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(imageRelativeUrl)
                .toUriString();

        // Veritabanına relative path kaydedebilirsin
        employee.setProfileImageUrl(imageRelativeUrl);
        employeeService.save(employee);

        // Frontend'e tam URL gönder
        return ResponseEntity.ok(Map.of("url", fullUrl));
    }



}




