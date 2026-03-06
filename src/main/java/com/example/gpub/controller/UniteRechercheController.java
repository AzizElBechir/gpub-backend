package com.example.gpub.controller;

import com.example.gpub.entity.Faculte;
import com.example.gpub.entity.UniteRecherche;
import com.example.gpub.repository.FaculteRepository;
import com.example.gpub.repository.UniteRechercheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Unites", description = "Research units management")
@RestController
@RequestMapping("/api/unites")
@CrossOrigin(origins = "*")
public class UniteRechercheController {

    @Autowired
    private UniteRechercheRepository uniteRepository;

    @Autowired
    private FaculteRepository faculteRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(uniteRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return uniteRepository.findById(id)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Unite de recherche not found")));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            String nom = (String) body.get("nom");
            Long faculteId = Long.valueOf(body.get("faculteId").toString());

            Faculte faculte = faculteRepository.findById(faculteId)
                .orElseThrow(() -> new RuntimeException("Faculte not found with id: " + faculteId));

            UniteRecherche unite = new UniteRecherche();
            unite.setNom(nom);
            unite.setFaculte(faculte);

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(uniteRepository.save(unite));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

   @PutMapping("/{id}")
public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
    Optional<UniteRecherche> optional = uniteRepository.findById(id);
    if (optional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "Unite de recherche not found"));
    }
    UniteRecherche u = optional.get();
    if (body.containsKey("nom")) u.setNom((String) body.get("nom"));
    if (body.get("faculteId") != null) {
        Long faculteId = Long.valueOf(body.get("faculteId").toString());
        faculteRepository.findById(faculteId).ifPresent(u::setFaculte);
    }
    return ResponseEntity.ok(uniteRepository.save(u));
}

@DeleteMapping("/{id}")
public ResponseEntity<?> delete(@PathVariable Long id) {
    if (!uniteRepository.existsById(id)) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of("error", "Unite de recherche not found"));
    }
    uniteRepository.deleteById(id);
    return ResponseEntity.ok(Map.of("message", "Unite de recherche deleted successfully"));
}
}