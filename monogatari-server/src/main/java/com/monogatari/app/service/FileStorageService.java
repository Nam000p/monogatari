package com.monogatari.app.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {
    @Value("${app.upload.dir}")
    private String uploadDirName;

    public String storeFile(MultipartFile file, String subPath) {
        try {
            Path uploadPath = Paths.get(uploadDirName).resolve(subPath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(subPath + "/")
                    .path(fileName)
                    .toUriString();
        } catch (IOException e) {
            log.error("Failed to store file in {}: {}", subPath, e.getMessage());
            throw new RuntimeException("Could not store file. Error: " + e.getMessage());
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            String identifier = "/uploads/";
            int index = fileUrl.indexOf(identifier);

            if (index != -1) {
                String relativePath = fileUrl.substring(index + identifier.length());
                Path filePath = Paths.get(uploadDirName).resolve(relativePath);
                Files.deleteIfExists(filePath);
                log.info("Deleted file at: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete file from URL {}: {}", fileUrl, e.getMessage());
        }
    }
}