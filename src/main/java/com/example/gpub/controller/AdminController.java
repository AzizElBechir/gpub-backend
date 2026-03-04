package com.example.gpub.controller;

import com.example.gpub.entity.Chercheur;
import com.example.gpub.entity.Publication;
import com.example.gpub.entity.StatPublicationJour;
import com.example.gpub.entity.Universite;
import com.example.gpub.repository.ChercheurRepository;
import com.example.gpub.repository.FaculteRepository;
import com.example.gpub.repository.PublicationRepository;
import com.example.gpub.repository.StatPublicationJourRepository;
import com.example.gpub.repository.UniteRechercheRepository;
import com.example.gpub.repository.UniversiteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin", description = "Admin dashboard - requires ADMIN or SUPER_ADMIN role")
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private ChercheurRepository chercheurRepository;

    @Autowired
    private UniversiteRepository universiteRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private StatPublicationJourRepository statRepository;

    @Autowired
    private FaculteRepository faculteRepository;

    @Autowired
    private UniteRechercheRepository uniteRepository;

    // ===== CHERCHEUR MANAGEMENT =====

    @GetMapping("/chercheurs")
    public ResponseEntity<?> getAllChercheurs(
            @RequestParam(required = false) String domaine,
            @RequestParam(required = false) Long universiteId,
            @RequestParam(required = false) Boolean actif,
            HttpServletRequest request) {
        try {
            String userRole = (String) request.getAttribute("userRole");
            Long userId = (Long) request.getAttribute("userId");

            List<Chercheur> chercheurs;

            if (userRole.equals("SUPER_ADMIN")) {
                chercheurs = chercheurRepository.findAll();
            } else {
                Chercheur admin = chercheurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

                if (admin.getAdminUniversite() == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin not linked to any university"));
                }

                chercheurs = chercheurRepository
                    .findByUniteRecherche_Faculte_Universite_Id(
                        admin.getAdminUniversite().getId()
                    );
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (Chercheur c : chercheurs) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", c.getId());
                item.put("nom", c.getNom());
                item.put("email", c.getEmail());
                item.put("domaine", c.getDomaine());
                item.put("role", c.getRole());
                item.put("actif", c.getActif());
                item.put("photoUrl", c.getPhotoUrl());
                if (c.getUniteRecherche() != null) {
                    item.put("uniteNom", c.getUniteRecherche().getNom());
                }
                result.add(item);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/chercheurs/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body,
            HttpServletRequest request) {
        try {
            String userRole = (String) request.getAttribute("userRole");
            Long userId = (Long) request.getAttribute("userId");

            Chercheur chercheur = chercheurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chercheur not found"));

            if (userRole.equals("ADMIN")) {
                Chercheur admin = chercheurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

                if (admin.getAdminUniversite() == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin not linked to any university"));
                }

                boolean sameUniversity = chercheur.getUniteRecherche() != null &&
                    chercheur.getUniteRecherche().getFaculte() != null &&
                    chercheur.getUniteRecherche().getFaculte().getUniversite() != null &&
                    chercheur.getUniteRecherche().getFaculte().getUniversite().getId()
                        .equals(admin.getAdminUniversite().getId());

                if (!sameUniversity) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only manage chercheurs from your university"));
                }
            }

            chercheur.setActif(body.get("actif"));
            chercheurRepository.save(chercheur);

            return ResponseEntity.ok(Map.of(
                "message", "Chercheur status updated",
                "id", id,
                "actif", chercheur.getActif()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/chercheurs/{id}/role")
    public ResponseEntity<?> assignRole(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        try {
            String userRole = (String) request.getAttribute("userRole");

            if (!userRole.equals("SUPER_ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only SUPER_ADMIN can assign roles"));
            }

            Chercheur chercheur = chercheurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chercheur not found"));

            String newRole = (String) body.get("role");
            chercheur.setRole(newRole);

            if (newRole.equals("ADMIN") && body.containsKey("universiteId")) {
                Long uniId = Long.valueOf(body.get("universiteId").toString());
                Universite universite = universiteRepository.findById(uniId)
                    .orElseThrow(() -> new RuntimeException("Universite not found"));
                chercheur.setAdminUniversite(universite);
            }

            if (!newRole.equals("ADMIN")) {
                chercheur.setAdminUniversite(null);
            }

            chercheurRepository.save(chercheur);

            return ResponseEntity.ok(Map.of(
                "message", "Role updated successfully",
                "id", id,
                "role", newRole
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ===== PUBLICATION MANAGEMENT =====

    @GetMapping("/publications")
    public ResponseEntity<?> getAllPublications(
            @RequestParam(required = false) String statut,
            HttpServletRequest request) {
        try {
            String userRole = (String) request.getAttribute("userRole");
            Long userId = (Long) request.getAttribute("userId");

            List<Publication> publications;

            if (userRole.equals("SUPER_ADMIN")) {
                publications = publicationRepository.findAll();
            } else {
                Chercheur admin = chercheurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

                if (admin.getAdminUniversite() == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin not linked to any university"));
                }

                publications = publicationRepository
                    .findByAuteurPrincipal_UniteRecherche_Faculte_Universite_Id(
                        admin.getAdminUniversite().getId()
                    );
            }

            if (statut != null && !statut.isEmpty()) {
                publications = publications.stream()
                    .filter(p -> statut.equalsIgnoreCase(p.getStatut()))
                    .collect(Collectors.toList());
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for (Publication p : publications) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", p.getId());
                item.put("titre", p.getTitre());
                item.put("domaine", p.getDomaine());
                item.put("statut", p.getStatut());
                item.put("datePublication", p.getDatePublication());
                item.put("pdfUrl", p.getPdfUrl());
                if (p.getAuteurPrincipal() != null) {
                    item.put("auteurNom", p.getAuteurPrincipal().getNom());
                    item.put("auteurId", p.getAuteurPrincipal().getId());
                }
                result.add(item);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/publications/{id}/statut")
    public ResponseEntity<?> moderatePublication(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            String userRole = (String) request.getAttribute("userRole");
            Long userId = (Long) request.getAttribute("userId");

            Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found"));

            if (userRole.equals("ADMIN")) {
                Chercheur admin = chercheurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

                if (admin.getAdminUniversite() == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin not linked to any university"));
                }

                boolean sameUniversity = publication.getAuteurPrincipal() != null &&
                    publication.getAuteurPrincipal().getUniteRecherche() != null &&
                    publication.getAuteurPrincipal().getUniteRecherche().getFaculte() != null &&
                    publication.getAuteurPrincipal().getUniteRecherche().getFaculte().getUniversite() != null &&
                    publication.getAuteurPrincipal().getUniteRecherche().getFaculte().getUniversite().getId()
                        .equals(admin.getAdminUniversite().getId());

                if (!sameUniversity) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only moderate publications from your university"));
                }
            }

            String newStatut = body.get("statut");
            if (!List.of("PUBLIE", "EN_ATTENTE", "RETIRE").contains(newStatut)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid statut. Must be PUBLIE, EN_ATTENTE or RETIRE"));
            }

            publication.setStatut(newStatut);
            publicationRepository.save(publication);

            return ResponseEntity.ok(Map.of(
                "message", "Publication statut updated",
                "id", id,
                "statut", newStatut
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    // ===== DASHBOARD STATS =====

    @GetMapping("/stats/global")
    public ResponseEntity<?> getGlobalStats(HttpServletRequest request) {
        try {
            String userRole = (String) request.getAttribute("userRole");
            Long userId = (Long) request.getAttribute("userId");

            List<Publication> publications;
            String scope;

            if (userRole.equals("SUPER_ADMIN")) {
                publications = publicationRepository.findAll();
                scope = "national";
            } else {
                Chercheur admin = chercheurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

                if (admin.getAdminUniversite() == null) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Admin not linked to any university"));
                }

                publications = publicationRepository
                    .findByAuteurPrincipal_UniteRecherche_Faculte_Universite_Id(
                        admin.getAdminUniversite().getId()
                    );
                scope = admin.getAdminUniversite().getNom();
            }

            // Totals
            int totalPublications = publications.size();
            int totalPublie = (int) publications.stream()
                .filter(p -> "PUBLIE".equals(p.getStatut())).count();
            int totalEnAttente = (int) publications.stream()
                .filter(p -> "EN_ATTENTE".equals(p.getStatut())).count();
            int totalRetire = (int) publications.stream()
                .filter(p -> "RETIRE".equals(p.getStatut())).count();

            // Views and downloads
            int totalVues = 0;
            int totalTelechargements = 0;
            for (Publication pub : publications) {
                List<StatPublicationJour> stats = statRepository.findByPublicationId(pub.getId());
                totalVues += stats.stream().mapToInt(StatPublicationJour::getVues).sum();
                totalTelechargements += stats.stream().mapToInt(StatPublicationJour::getTelechargements).sum();
            }

            // By domain
            Map<String, Integer> byDomaine = new LinkedHashMap<>();
            for (Publication pub : publications) {
                if (pub.getDomaine() != null) {
                    byDomaine.merge(pub.getDomaine(), 1, Integer::sum);
                }
            }

            // By university (SUPER_ADMIN only)
            List<Map<String, Object>> byUniversite = new ArrayList<>();
            if (userRole.equals("SUPER_ADMIN")) {
                List<Universite> universites = universiteRepository.findAll();
                for (Universite u : universites) {
                    List<Publication> uPubs = publicationRepository
                        .findByAuteurPrincipal_UniteRecherche_Faculte_Universite_Id(u.getId());

                    int uVues = 0;
                    int uTel = 0;
                    for (Publication pub : uPubs) {
                        List<StatPublicationJour> stats = statRepository.findByPublicationId(pub.getId());
                        uVues += stats.stream().mapToInt(StatPublicationJour::getVues).sum();
                        uTel += stats.stream().mapToInt(StatPublicationJour::getTelechargements).sum();
                    }

                    Map<String, Object> uStats = new LinkedHashMap<>();
                    uStats.put("universiteId", u.getId());
                    uStats.put("universiteNom", u.getNom());
                    uStats.put("totalPublications", uPubs.size());
                    uStats.put("totalVues", uVues);
                    uStats.put("totalTelechargements", uTel);
                    byUniversite.add(uStats);
                }
            }

            // Build response
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("scope", scope);
            result.put("totalPublications", totalPublications);
            result.put("totalPublie", totalPublie);
            result.put("totalEnAttente", totalEnAttente);
            result.put("totalRetire", totalRetire);
            result.put("totalVues", totalVues);
            result.put("totalTelechargements", totalTelechargements);
            result.put("byDomaine", byDomaine);
            if (userRole.equals("SUPER_ADMIN")) {
                result.put("byUniversite", byUniversite);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }
}
