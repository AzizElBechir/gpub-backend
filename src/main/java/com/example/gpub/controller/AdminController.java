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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

            if (userRole == null || userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token manquant ou invalide. Reconnectez-vous."));
            }

            Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found"));

            if ("ADMIN".equals(userRole)) {
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

            int totalPublications = publications.size();
            int totalPublie = (int) publications.stream()
                .filter(p -> "PUBLIE".equals(p.getStatut())).count();
            int totalEnAttente = (int) publications.stream()
                .filter(p -> "EN_ATTENTE".equals(p.getStatut())).count();
            int totalRetire = (int) publications.stream()
                .filter(p -> "RETIRE".equals(p.getStatut())).count();

            int totalVues = 0;
            int totalTelechargements = 0;
            for (Publication pub : publications) {
                List<StatPublicationJour> stats = statRepository.findByPublicationId(pub.getId());
                totalVues += stats.stream().mapToInt(StatPublicationJour::getVues).sum();
                totalTelechargements += stats.stream().mapToInt(StatPublicationJour::getTelechargements).sum();
            }

            Map<String, Integer> byDomaine = new LinkedHashMap<>();
            for (Publication pub : publications) {
                if (pub.getDomaine() != null) {
                    byDomaine.merge(pub.getDomaine(), 1, Integer::sum);
                }
            }

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

            long totalChercheurs = chercheurRepository.count();
            long totalUniversites = universiteRepository.count();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("scope", scope);
            result.put("totalPublications", totalPublications);
            result.put("totalPublie", totalPublie);
            result.put("totalEnAttente", totalEnAttente);
            result.put("totalRetire", totalRetire);
            result.put("totalVues", totalVues);
            result.put("totalTelechargements", totalTelechargements);
            result.put("totalChercheurs", totalChercheurs);
            result.put("totalUniversites", totalUniversites);
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

    // ===== AUDIT LOGS =====

    @GetMapping("/audit-logs")
    public ResponseEntity<?> getAuditLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String utilisateur,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        try {
            String userRole = (String) request.getAttribute("userRole");
            Long userId = (Long) request.getAttribute("userId");

            if (!userRole.equals("SUPER_ADMIN") && !userRole.equals("ADMIN")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access denied"));
            }

            List<Map<String, Object>> logs = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // Generate logs from publications changes
            List<Publication> publications;
            if (userRole.equals("SUPER_ADMIN")) {
                publications = publicationRepository.findAll();
            } else {
                Chercheur admin = chercheurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
                publications = admin.getAdminUniversite() != null
                    ? publicationRepository.findByAuteurPrincipal_UniteRecherche_Faculte_Universite_Id(
                        admin.getAdminUniversite().getId())
                    : new ArrayList<>();
            }

            for (Publication pub : publications) {
                // Log creation
                Map<String, Object> logCreate = new LinkedHashMap<>();
                logCreate.put("id", "pub-create-" + pub.getId());
                logCreate.put("action", "PUBLICATION_CREATED");
                logCreate.put("actionLabel", "Publication créée");
                logCreate.put("target", pub.getTitre());
                logCreate.put("targetId", pub.getId());
                logCreate.put("targetType", "PUBLICATION");
                logCreate.put("utilisateur", pub.getAuteurPrincipal() != null
                    ? pub.getAuteurPrincipal().getNom() : "Inconnu");
                logCreate.put("utilisateurId", pub.getAuteurPrincipal() != null
                    ? pub.getAuteurPrincipal().getId() : null);
                logCreate.put("universite", getUniversiteNom(pub));
                logCreate.put("statut", pub.getStatut());
                logCreate.put("time", pub.getCreatedAt() != null
                    ? pub.getCreatedAt().format(formatter) : "N/A");
                logCreate.put("timestamp", pub.getCreatedAt() != null
                    ? pub.getCreatedAt().toString() : null);
                logs.add(logCreate);

                // Log moderation if published or retired
                if ("PUBLIE".equals(pub.getStatut()) || "RETIRE".equals(pub.getStatut())) {
                    Map<String, Object> logMod = new LinkedHashMap<>();
                    logMod.put("id", "pub-mod-" + pub.getId());
                    logMod.put("action", "PUBLIE".equals(pub.getStatut())
                        ? "PUBLICATION_APPROVED" : "PUBLICATION_RETIRED");
                    logMod.put("actionLabel", "PUBLIE".equals(pub.getStatut())
                        ? "Publication approuvée" : "Publication retirée");
                    logMod.put("target", pub.getTitre());
                    logMod.put("targetId", pub.getId());
                    logMod.put("targetType", "PUBLICATION");
                    logMod.put("utilisateur", "Admin");
                    logMod.put("utilisateurId", null);
                    logMod.put("universite", getUniversiteNom(pub));
                    logMod.put("statut", pub.getStatut());
                    logMod.put("time", pub.getUpdatedAt() != null
                        ? pub.getUpdatedAt().format(formatter) : "N/A");
                    logMod.put("timestamp", pub.getUpdatedAt() != null
                        ? pub.getUpdatedAt().toString() : null);
                    logs.add(logMod);
                }
            }

            // Add chercheur registration logs
            if (userRole.equals("SUPER_ADMIN")) {
                List<Chercheur> chercheurs = chercheurRepository.findAll();
                for (Chercheur c : chercheurs) {
                    Map<String, Object> logReg = new LinkedHashMap<>();
                    logReg.put("id", "user-reg-" + c.getId());
                    logReg.put("action", "USER_REGISTERED");
                    logReg.put("actionLabel", "Chercheur inscrit");
                    logReg.put("target", c.getNom() + " (" + c.getEmail() + ")");
                    logReg.put("targetId", c.getId());
                    logReg.put("targetType", "CHERCHEUR");
                    logReg.put("utilisateur", c.getNom());
                    logReg.put("utilisateurId", c.getId());
                    logReg.put("universite", c.getUniteRecherche() != null &&
                        c.getUniteRecherche().getFaculte() != null &&
                        c.getUniteRecherche().getFaculte().getUniversite() != null
                        ? c.getUniteRecherche().getFaculte().getUniversite().getNom() : "N/A");
                    logReg.put("statut", c.getActif() ? "ACTIF" : "INACTIF");
                    logReg.put("time", c.getDateCreation() != null
                        ? c.getDateCreation().format(formatter) : "N/A");
                    logReg.put("timestamp", c.getDateCreation() != null
                        ? c.getDateCreation().toString() : null);
                    logs.add(logReg);
                }
            }

            // Filter by action
            if (action != null && !action.isEmpty()) {
                logs = logs.stream()
                    .filter(l -> action.equalsIgnoreCase((String) l.get("action")))
                    .collect(Collectors.toList());
            }

            // Filter by user
            if (utilisateur != null && !utilisateur.isEmpty()) {
                String searchUser = utilisateur.toLowerCase();
                logs = logs.stream()
                    .filter(l -> l.get("utilisateur") != null &&
                        ((String) l.get("utilisateur")).toLowerCase().contains(searchUser))
                    .collect(Collectors.toList());
            }

            // Sort by timestamp descending (most recent first)
            logs.sort((a, b) -> {
                String ta = (String) a.get("timestamp");
                String tb = (String) b.get("timestamp");
                if (ta == null && tb == null) return 0;
                if (ta == null) return 1;
                if (tb == null) return -1;
                return tb.compareTo(ta);
            });

            // Pagination
            int total = logs.size();
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, total);
            List<Map<String, Object>> pagedLogs = fromIndex < total
                ? logs.subList(fromIndex, toIndex)
                : new ArrayList<>();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("logs", pagedLogs);
            response.put("totalElements", total);
            response.put("totalPages", (int) Math.ceil((double) total / size));
            response.put("currentPage", page);
            response.put("size", size);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
        }
    }

    private String getUniversiteNom(Publication pub) {
        try {
            return pub.getAuteurPrincipal().getUniteRecherche()
                .getFaculte().getUniversite().getNom();
        } catch (Exception e) {
            return "N/A";
        }
    }
}