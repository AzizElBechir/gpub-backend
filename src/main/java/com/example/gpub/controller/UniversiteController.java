package com.example.gpub.controller;

import com.example.gpub.entity.Universite;
import com.example.gpub.repository.UniversiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
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
    public ResponseEntity<?> create(@RequestBody Universite universite) {
        try {
            Universite saved = universiteRepository.save(universite);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
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
        u.setNom(updated.getNom());
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