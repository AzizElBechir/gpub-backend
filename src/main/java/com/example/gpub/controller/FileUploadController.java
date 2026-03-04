package com.example.gpub.controller;

import com.example.gpub.entity.Chercheur;
import com.example.gpub.repository.ChercheurRepository;
import com.example.gpub.service.FileUploadService;
import com.example.gpub.service.StatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Files", description = "PDF and photo upload/download")
@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Autowired
    private ChercheurRepository chercheurRepository;

    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private StatService statService;  

// Upload profile photo for a researcher
@PostMapping("/upload/photo/{chercheurId}")
public ResponseEntity<?> uploadPhoto(
        @RequestParam("file") MultipartFile file,
        @PathVariable Long chercheurId,
        HttpServletRequest request) {
    try {
        Long userId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        // Check ownership
        if (!userRole.equals("ADMIN") && !chercheurId.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "You can only update your own photo"));
        }

        // Validate image file
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Only image files are allowed (jpg, png, gif, etc.)"));
        }

        // Max size 2MB for photos
        if (file.getSize() > 2 * 1024 * 1024) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Photo size must be less than 2MB"));
        }

        // Upload the file
      String fileUrl = fileUploadService.uploadFile(file);
        // Update chercheur's photo_url in database
        Chercheur chercheur = chercheurRepository.findById(chercheurId)
            .orElseThrow(() -> new RuntimeException("Chercheur not found with id: " + chercheurId));

        // Delete old photo if exists
        if (chercheur.getPhotoUrl() != null) {
          fileUploadService.deletePDF(chercheur.getPhotoUrl());
        }

        chercheur.setPhotoUrl(fileUrl);
        chercheurRepository.save(chercheur);

        return ResponseEntity.ok(Map.of(
            "message", "Photo uploaded successfully",
            "photoUrl", fileUrl,
            "chercheurId", chercheurId
        ));

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", e.getMessage()));
    }
}

    // Upload PDF with better error handling
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            // Check if file is null
            if (file == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No file provided. Please select a file.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Check if file is empty
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "File is empty. Please select a valid file.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // Log file info
            System.out.println("Received file: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Content type: " + file.getContentType());
            
            String fileUrl = fileUploadService.uploadPDF(file);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "File uploaded successfully");
            response.put("fileUrl", fileUrl);
            response.put("filename", file.getOriginalFilename());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            e.printStackTrace(); // Print full error to console
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace(); // Print full error to console
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Download/View PDF - Track download
    @GetMapping("/download/**")
    public ResponseEntity<Resource> downloadFile(HttpServletRequest request) {
        try {
            // Get the full path after /download/
            String fullPath = request.getRequestURI().split("/download/")[1];
            
            // Remove /uploads/ prefix if present
            String filename = fullPath.replace("uploads/", "").replace("/uploads/", "");
            
            Path filePath = fileUploadService.getFilePath(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                
                // Try to extract publication ID from request parameter
                String publicationIdParam = request.getParameter("publicationId");
                if (publicationIdParam != null) {
                    try {
                        Long publicationId = Long.parseLong(publicationIdParam);
                        statService.incrementTelechargements(publicationId);
                    } catch (Exception e) {
                        System.err.println("Failed to track download: " + e.getMessage());
                    }
                }
                
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete PDF
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            fileUploadService.deletePDF(fileUrl);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "File deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}