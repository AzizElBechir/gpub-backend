package com.example.gpub.controller;

import com.example.gpub.repository.UniversiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint de diagnostic : vérifie que le backend et la base de données répondent.
 * GET /api/health → 200 si OK, 503 si la DB est inaccessible.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private UniversiteRepository universiteRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", "up");
        try {
            long count = universiteRepository.count();
            body.put("database", "connected");
            body.put("universitesCount", count);
        } catch (Exception e) {
            body.put("database", "error");
            body.put("error", e.getMessage());
            return ResponseEntity.status(503).body(body);
        }
        return ResponseEntity.ok(body);
    }
}
