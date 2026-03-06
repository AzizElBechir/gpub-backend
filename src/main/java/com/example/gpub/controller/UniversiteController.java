package com.example.gpub.controller;

import com.example.gpub.entity.Universite;
import com.example.gpub.repository.UniversiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Universites", description = "Universities management")
@RestController
@RequestMapping("/api/universites")
@CrossOrigin(origins = "*")
public class UniversiteController {

    @Autowired
    private UniversiteRepository universiteRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(universiteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Universite> optional = universiteRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Universite not found"));
        }
        return ResponseEntity.ok(optional.get());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        try {
            String nom = body != null ? (body.get("nom") != null ? body.get("nom").trim() : null) : null;
            if (nom == null || nom.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Le nom de l'université est obligatoire."));
            }
            Universite universite = new Universite();
            universite.setNom(nom);
            if (body != null) {
                if (body.get("ville") != null) universite.setVille(body.get("ville").trim().isEmpty() ? null : body.get("ville").trim());
                if (body.get("email") != null) universite.setEmail(body.get("email").trim().isEmpty() ? null : body.get("email").trim());
            }
            Universite saved = universiteRepository.save(universite);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Une université avec ce nom existe déjà."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Erreur lors de la création."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Universite updated) {
        Optional<Universite> optional = universiteRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Universite not found"));
        }
        Universite u = optional.get();
        if (updated.getNom() != null) u.setNom(updated.getNom());
        if (updated.getVille() != null) u.setVille(updated.getVille().trim().isEmpty() ? null : updated.getVille().trim());
        if (updated.getEmail() != null) u.setEmail(updated.getEmail().trim().isEmpty() ? null : updated.getEmail().trim());
        return ResponseEntity.ok(universiteRepository.save(u));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!universiteRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Universite not found"));
        }
        universiteRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Universite deleted successfully"));
    }
}