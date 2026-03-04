package com.example.gpub.controller;

import com.example.gpub.service.StatService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Statistics", description = "Views and downloads tracking")
@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatController {

    @Autowired
    private StatService statService;

    // Public - Get stats for a specific publication
    @GetMapping("/publication/{id}")
    public ResponseEntity<Map<String, Object>> getPublicationStats(@PathVariable Long id) {
        Map<String, Object> stats = statService.getPublicationStats(id);
        return ResponseEntity.ok(stats);
    }

    // Protected - Get stats for logged-in chercheur
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyChercheurStats(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        Map<String, Object> stats = statService.getChercheurStats(userId);
        return ResponseEntity.ok(stats);
    }

    // Protected - Detailed stats with date range
    @GetMapping("/me/detail")
    public ResponseEntity<?> getMyDetailedStats(
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            LocalDate debut = dateDebut != null ? LocalDate.parse(dateDebut) : null;
            LocalDate fin = dateFin != null ? LocalDate.parse(dateFin) : null;

            Map<String, Object> stats = statService.getChercheurDetailedStats(userId, debut, fin);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // Public - Get stats for specific chercheur by ID
    @GetMapping("/chercheur/{id}")
    public ResponseEntity<Map<String, Object>> getChercheurStats(@PathVariable Long id) {
        Map<String, Object> stats = statService.getChercheurStats(id);
        return ResponseEntity.ok(stats);
    }
}
