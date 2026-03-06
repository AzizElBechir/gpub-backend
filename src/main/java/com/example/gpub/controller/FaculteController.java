package com.example.gpub.controller;

import com.example.gpub.entity.Faculte;
import com.example.gpub.entity.Universite;
import com.example.gpub.repository.FaculteRepository;
import com.example.gpub.repository.UniversiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Facultes", description = "Faculties management")
@RestController
@RequestMapping("/api/facultes")
@CrossOrigin(origins = "*")
public class FaculteController {

    @Autowired
    private FaculteRepository faculteRepository;

    @Autowired
    private UniversiteRepository universiteRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(faculteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Faculte> optional = faculteRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Faculte not found"));
        }
        return ResponseEntity.ok(optional.get());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            String nom = (String) body.get("nom");
            Long universiteId = Long.valueOf(body.get("universiteId").toString());

            Universite universite = universiteRepository.findById(universiteId)
                .orElseThrow(() -> new RuntimeException("Universite not found with id: " + universiteId));

            Faculte faculte = new Faculte();
            faculte.setNom(nom);
            faculte.setUniversite(universite);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(faculteRepository.save(faculte));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<Faculte> optional = faculteRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Faculte not found"));
        }
        Faculte f = optional.get();
        if (body.containsKey("nom")) f.setNom((String) body.get("nom"));
        if (body.get("universiteId") != null) {
            Long universiteId = Long.valueOf(body.get("universiteId").toString());
            universiteRepository.findById(universiteId).ifPresent(f::setUniversite);
        }
        return ResponseEntity.ok(faculteRepository.save(f));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!faculteRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Faculte not found"));
        }
        faculteRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Faculte deleted successfully"));
    }
}