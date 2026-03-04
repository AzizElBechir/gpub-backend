package com.example.gpub.controller;

import com.example.gpub.dto.ChercheurDTO;
import com.example.gpub.dto.LoginRequest;
import com.example.gpub.dto.LoginResponse;
import com.example.gpub.entity.Chercheur;
import com.example.gpub.service.AuthService;
import com.example.gpub.service.ChercheurService;
import com.example.gpub.repository.ChercheurRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Tag(name = "Authentication", description = "Login, register, password management")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ChercheurService chercheurService;

    @Autowired
    private ChercheurRepository chercheurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String email = (String) request.getAttribute("userEmail");
        String role = (String) request.getAttribute("userRole");

        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid or missing token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Token is valid");
        response.put("userId", userId);
        response.put("email", email);
        response.put("role", role);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody ChercheurDTO chercheurDTO) {
        try {
            ChercheurDTO created = chercheurService.createChercheur(chercheurDTO);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("user", created);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            String currentPassword = body.get("currentPassword");
            String newPassword = body.get("newPassword");

            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "currentPassword and newPassword are required"));
            }

            if (newPassword.length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "New password must be at least 6 characters"));
            }

            Chercheur chercheur = chercheurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(currentPassword, chercheur.getHashMdp())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Current password is incorrect"));
            }

            chercheur.setHashMdp(passwordEncoder.encode(newPassword));
            chercheurRepository.save(chercheur);

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Chercheur chercheur = chercheurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> profile = new LinkedHashMap<>();
            profile.put("id", chercheur.getId());
            profile.put("nom", chercheur.getNom());
            profile.put("email", chercheur.getEmail());
            profile.put("role", chercheur.getRole());
            profile.put("bio", chercheur.getBio());
            profile.put("domaine", chercheur.getDomaine());
            profile.put("photoUrl", chercheur.getPhotoUrl());
            profile.put("actif", chercheur.getActif());
            profile.put("dateCreation", chercheur.getDateCreation());
            if (chercheur.getUniteRecherche() != null) {
                profile.put("uniteId", chercheur.getUniteRecherche().getId());
                profile.put("uniteNom", chercheur.getUniteRecherche().getNom());
            }
            if (chercheur.getAdminUniversite() != null) {
                profile.put("adminUniversiteId", chercheur.getAdminUniversite().getId());
                profile.put("adminUniversiteNom", chercheur.getAdminUniversite().getNom());
            }
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ⚠️ TEMPORARY - DELETE AFTER USE
    @GetMapping("/setup-admin")
    public ResponseEntity<?> setupAdmin() {
        try {
            if (chercheurRepository.findByEmail("superadmin@ministere.mr").isPresent()) {
                return ResponseEntity.ok(Map.of("message", "Super Admin already exists!"));
            }
            Chercheur admin = new Chercheur();
            admin.setNom("Super Admin");
            admin.setEmail("superadmin@ministere.mr");
            admin.setHashMdp(passwordEncoder.encode("admin123"));
            admin.setRole("SUPER_ADMIN");
            admin.setActif(true);
            admin.setDateCreation(LocalDateTime.now());
            chercheurRepository.save(admin);
            return ResponseEntity.ok(Map.of("message", "Super Admin created successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}