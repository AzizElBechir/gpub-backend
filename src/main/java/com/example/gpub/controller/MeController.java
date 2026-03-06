package com.example.gpub.controller;

import com.example.gpub.dto.PublicationDTO;
import com.example.gpub.service.PublicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Me", description = "Current user's resources")
@RestController
@RequestMapping("/api/me")
@CrossOrigin(origins = "*")
public class MeController {

    @Autowired
    private PublicationService publicationService;

    @GetMapping("/publications")
    public ResponseEntity<Page<PublicationDTO>> getMyPublications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String statut,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        Page<PublicationDTO> publications = publicationService.getMyPublications(userId, page, size, statut);
        return ResponseEntity.ok(publications);
    }
}
