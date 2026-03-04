package com.example.gpub.controller;

import com.example.gpub.dto.ChercheurDTO;
import com.example.gpub.dto.PublicationDTO;
import com.example.gpub.service.ChercheurService;
import com.example.gpub.service.PublicationService;
import com.example.gpub.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authors", description = "Public author profiles")
@RestController
@RequestMapping("/api/auteurs")
@CrossOrigin(origins = "*")
public class AuthorController {

    @Autowired
    private ChercheurService chercheurService;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private StatService statService;

    // Public - Full author profile with publications and stats
    @GetMapping("/{id}")
    public ResponseEntity<?> getAuthorProfile(@PathVariable Long id) {
        try {
            // Get author info
            ChercheurDTO chercheur = chercheurService.getChercheurById(id);

            // Get author's publications
            List<PublicationDTO> publications = publicationService.getPublicationsByAuteur(id);

            // Get author's stats
            Map<String, Object> stats = statService.getChercheurStats(id);

            // Build response
            Map<String, Object> profile = new LinkedHashMap<>();
            profile.put("id", chercheur.getId());
            profile.put("nom", chercheur.getNom());
            profile.put("email", chercheur.getEmail());
            profile.put("bio", chercheur.getBio());
            profile.put("domaine", chercheur.getDomaine());
            profile.put("photoUrl", chercheur.getPhotoUrl());
            profile.put("uniteNom", chercheur.getUniteNom());
            profile.put("totalPublications", publications.size());
            profile.put("totalVues", stats.get("totalVues"));
            profile.put("totalTelechargements", stats.get("totalTelechargements"));
            profile.put("publications", publications);

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    // Public - List all authors with their publication count
    @GetMapping
    public ResponseEntity<?> getAllAuthors() {
        try {
            List<ChercheurDTO> chercheurs = chercheurService.getAllChercheurs();

            List<Map<String, Object>> result = new ArrayList<>();
            for (ChercheurDTO c : chercheurs) {
                List<PublicationDTO> pubs = publicationService.getPublicationsByAuteur(c.getId());
                Map<String, Object> stats = statService.getChercheurStats(c.getId());

                Map<String, Object> author = new LinkedHashMap<>();
                author.put("id", c.getId());
                author.put("nom", c.getNom());
                author.put("domaine", c.getDomaine());
                author.put("bio", c.getBio());
                author.put("photoUrl", c.getPhotoUrl());
                author.put("uniteNom", c.getUniteNom());
                author.put("totalPublications", pubs.size());
                author.put("totalVues", stats.get("totalVues"));
                result.add(author);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}