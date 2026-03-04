package com.example.gpub.controller;

import com.example.gpub.entity.Publication;
import com.example.gpub.repository.PublicationRepository;
import com.example.gpub.service.CoAuteurService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Co-Authors", description = "Manage co-authors on publications")
@RestController
@RequestMapping("/api/publications/{publicationId}/coauteurs")
@CrossOrigin(origins = "*")
public class CoAuteurController {

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private CoAuteurService coAuteurService;

    // Public - Get all co-authors of a publication
    @GetMapping
    public ResponseEntity<?> getCoAuteurs(@PathVariable Long publicationId) {
        try {
            List<Map<String, Object>> coauteurs = coAuteurService.getCoAuteurs(publicationId);
            return ResponseEntity.ok(coauteurs);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // Protected - Add co-author (requires auth)
    @PostMapping("/{chercheurId}")
public ResponseEntity<?> addCoAuteur(
        @PathVariable Long publicationId,
        @PathVariable Long chercheurId,
        HttpServletRequest request) {
    try {
        Long userId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        // Check ownership
        Publication publication = publicationRepository.findById(publicationId)
            .orElseThrow(() -> new RuntimeException("Publication not found"));

        if (!userRole.equals("ADMIN") && !publication.getAuteurPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "You can only manage co-authors of your own publications"));
        }

        Map<String, Object> result = coAuteurService.addCoAuteur(publicationId, chercheurId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", e.getMessage()));
    }
}

@DeleteMapping("/{chercheurId}")
public ResponseEntity<?> removeCoAuteur(
        @PathVariable Long publicationId,
        @PathVariable Long chercheurId,
        HttpServletRequest request) {
    try {
        Long userId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        Publication publication = publicationRepository.findById(publicationId)
            .orElseThrow(() -> new RuntimeException("Publication not found"));

        if (!userRole.equals("ADMIN") && !publication.getAuteurPrincipal().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "You can only manage co-authors of your own publications"));
        }

        Map<String, Object> result = coAuteurService.removeCoAuteur(publicationId, chercheurId);
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", e.getMessage()));
    }
}
}