package com.example.gpub.controller;

import com.example.gpub.dto.ChercheurDTO;
import com.example.gpub.service.ChercheurService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Chercheurs", description = "Researcher profiles management")
@RestController
@RequestMapping("/api/chercheurs")
@CrossOrigin(origins = "*")
public class ChercheurController {
    
    @Autowired
    private ChercheurService chercheurService;
    
    /** GET /api/chercheurs — liste tous les chercheurs. Paramètre optionnel : search (ou nom, query) pour filtrer par nom/email. */
    @GetMapping
    public ResponseEntity<List<ChercheurDTO>> getChercheurs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String query) {
        String term = search != null ? search : (nom != null ? nom : query);
        List<ChercheurDTO> chercheurs = chercheurService.searchChercheurs(term);
        return ResponseEntity.ok(chercheurs);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ChercheurDTO> getChercheurById(@PathVariable Long id) {
        ChercheurDTO chercheur = chercheurService.getChercheurById(id);
        return ResponseEntity.ok(chercheur);
    }
    
    // UPDATED: Added better error handling
    @PostMapping
    public ResponseEntity<?> createChercheur(@RequestBody ChercheurDTO chercheurDTO) {
        try {
            ChercheurDTO created = chercheurService.createChercheur(chercheurDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("type", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @PutMapping("/{id}")
public ResponseEntity<?> update(
        @PathVariable Long id,
        @RequestBody ChercheurDTO chercheurDTO,
        HttpServletRequest request) {
    try {
        Long userId = (Long) request.getAttribute("userId");
        String userRole = (String) request.getAttribute("userRole");

        if (!userRole.equals("ADMIN") && !id.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "You can only edit your own profile"));
        }

        ChercheurDTO updated = chercheurService.updateChercheur(id, chercheurDTO);
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

        if (!userRole.equals("ADMIN") && !id.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "You can only delete your own profile"));
        }

        chercheurService.deleteChercheur(id);
        return ResponseEntity.ok(Map.of("message", "Chercheur deleted successfully"));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", e.getMessage()));
    }
}
}