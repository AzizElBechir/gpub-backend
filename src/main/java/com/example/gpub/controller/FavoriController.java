package com.example.gpub.controller;

import com.example.gpub.service.FavoriService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Favorites", description = "Manage favorite publications")
@RestController
@CrossOrigin(origins = "*")
public class FavoriController {

    @Autowired
    private FavoriService favoriService;

    @GetMapping("/api/me/favoris")
    public ResponseEntity<?> getFavoris(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<Map<String, Object>> favoris = favoriService.getFavoris(userId);
            return ResponseEntity.ok(favoris);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/publications/{id}/favori")
    public ResponseEntity<?> addFavori(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Map<String, Object> result = favoriService.addFavori(userId, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg.contains("already in favorites")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", msg));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", msg));
        }
    }

    @DeleteMapping("/api/publications/{id}/favori")
    public ResponseEntity<?> removeFavori(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Map<String, Object> result = favoriService.removeFavori(userId, id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/publications/{id}/favori")
    public ResponseEntity<?> isFavori(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Map<String, Object> result = favoriService.isFavori(userId, id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}