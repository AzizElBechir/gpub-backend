package com.example.gpub.controller;

import com.example.gpub.dto.PublicationDTO;
import com.example.gpub.entity.Publication;
import com.example.gpub.service.PublicationService;
import com.example.gpub.service.StatService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.gpub.repository.PublicationRepository;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Publications", description = "CRUD publications, search, filter")
@RestController
@RequestMapping("/api/publications")
@CrossOrigin(origins = "*")
public class PublicationController {
    
    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private PublicationService publicationService;
    
    @Autowired
    private StatService statService;
    
    // Public endpoint - Search and filter publications
    @GetMapping
    public ResponseEntity<Page<PublicationDTO>> searchPublications(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String domaine,
            @RequestParam(required = false) String auteur,
            @RequestParam(required = false) Integer annee,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Page<PublicationDTO> publications = publicationService.searchPublications(
            query, domaine, auteur, annee, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(publications);
    }
    
    // Public endpoint - Get single publication (track view)
    @GetMapping("/{id}")
    public ResponseEntity<PublicationDTO> getPublicationById(@PathVariable Long id) {
        PublicationDTO publication = publicationService.getPublicationById(id);
        
        // Track view
        try {
            statService.incrementVues(id);
        } catch (Exception e) {
            // Don't fail the request if stat tracking fails
            System.err.println("Failed to track view: " + e.getMessage());
        }
        
        return ResponseEntity.ok(publication);
    }
    
    

    @PostMapping
    public ResponseEntity<?> createPublication(@RequestBody PublicationDTO publicationDTO) {
        try {
            PublicationDTO created = publicationService.createPublication(publicationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Protected endpoint - Update publication (requires authentication)
    @PutMapping("/{id}")
public ResponseEntity<?> update(
        @PathVariable Long id,
        @RequestBody PublicationDTO publicationDTO,
        HttpServletRequest request) {
    try {
        Long userId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        // Check ownership
        Publication existing = publicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Publication not found"));

        if (!userRole.equals("ADMIN") && !existing.getAuteurPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "You can only edit your own publications"));
        }

        PublicationDTO updated = publicationService.updatePublication(id, publicationDTO);
        return ResponseEntity.ok(updated);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", e.getMessage()));
    }
}

@DeleteMapping("/{id}")
public ResponseEntity<?> delete(
        @PathVariable Long id,
        HttpServletRequest request) {
    try {
        Long userId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        // Check ownership
        Publication existing = publicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Publication not found"));

        if (!userRole.equals("ADMIN") && !existing.getAuteurPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "You can only delete your own publications"));
        }

        publicationService.deletePublication(id);
        return ResponseEntity.ok(Map.of("message", "Publication deleted successfully"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", e.getMessage()));
    }
}
}