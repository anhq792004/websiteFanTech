package com.example.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {
    
    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;
    
    @Autowired
    @Qualifier("getUploadDirectory")
    private String uploadDirectory;
    
    /**
     * 
     * @param file 
     * @return 
     * @throws IOException 
     */
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        
        File uploadDirFile = new File(uploadDirectory);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }
        
        String fileExtension = getFileExtension(originalFilename);
        validateFileExtension(fileExtension);
        
        String fileName = UUID.randomUUID() + fileExtension;
        
        // Lưu file
        Path filePath = Paths.get(uploadDirectory, fileName);
        Files.write(filePath, file.getBytes());
        
        // Trả về đường dẫn tương đối
        return "/uploads/" + fileName;
    }
    
    /**
     * 
     * @param relativePath 
     * @return 
     */
    public boolean deleteFile(String relativePath) {
        if (relativePath == null || !relativePath.startsWith("/uploads/")) {
            return false;
        }
        
        String fileName = relativePath.substring("/uploads/".length());
        Path filePath = Paths.get(uploadDirectory, fileName);
        
        try {
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 
     * @param relativePath 
     * @return 
     */
    public boolean fileExists(String relativePath) {
        if (relativePath == null || !relativePath.startsWith("/uploads/")) {
            return false;
        }
        
        String fileName = relativePath.substring("/uploads/".length());
        Path filePath = Paths.get(uploadDirectory, fileName);
        
        return Files.exists(filePath);
    }
    
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("File phải có extension");
        }
        return filename.substring(lastDotIndex);
    }
    
    private void validateFileExtension(String extension) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
        String lowerExtension = extension.toLowerCase();
        
        for (String allowed : allowedExtensions) {
            if (allowed.equals(lowerExtension)) {
                return;
            }
        }
        
        throw new IllegalArgumentException("Chỉ cho phép file hình ảnh: " + String.join(", ", allowedExtensions));
    }
    
    public String getUploadDirectory() {
        return uploadDirectory;
    }
} 