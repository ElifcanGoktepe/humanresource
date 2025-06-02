package com.project.humanresource.service;

import com.project.humanresource.exception.ErrorType;
import com.project.humanresource.exception.HumanResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:5242880}") // 5MB default
    private long maxFileSize;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif"
    );

    /**
     * Profil fotoğrafı yükleme
     */
    public String uploadProfileImage(MultipartFile file, Long employeeId, String oldImageUrl) {
        validateFile(file);

        try {
            // Önce eski dosyayı sil
            deleteProfileImage(oldImageUrl);

            // Upload dizinini oluştur
            Path uploadPath = Paths.get(uploadDir, "profile-images");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Dosya adını oluştur
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String fileName = "profile_" + employeeId + "_" + UUID.randomUUID() + "." + fileExtension;

            // Dosyayı kaydet
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // URL'i döndür
            return "/uploads/profile-images/" + fileName;

        } catch (IOException e) {
            throw new HumanResourceException(ErrorType.FILE_UPLOAD_ERROR);
        }
    }


    /**
     * Dosya validasyonu
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new HumanResourceException(ErrorType.FILE_NOT_FOUND);
        }

        // Dosya boyutu kontrolü
        if (file.getSize() > maxFileSize) {
            throw new HumanResourceException(ErrorType.FILE_SIZE_TOO_LARGE);
        }

        // MIME type kontrolü
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new HumanResourceException(ErrorType.INVALID_FILE_TYPE);
        }

        // Dosya uzantısı kontrolü
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new HumanResourceException(ErrorType.INVALID_FILE_NAME);
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new HumanResourceException(ErrorType.INVALID_FILE_EXTENSION);
        }
    }

    /**
     * Dosya uzantısını al
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    /**
     * Eski profil fotoğrafını sil
     */
    public void deleteProfileImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // URL'den dosya yolunu çıkar
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            Path filePath = Paths.get(uploadDir, "profile-images", fileName);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Log the error but don't throw exception
            System.err.println("Failed to delete profile image: " + imageUrl);
        }
    }
} 