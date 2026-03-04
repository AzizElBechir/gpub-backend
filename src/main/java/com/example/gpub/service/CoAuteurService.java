package com.example.gpub.service;

import com.example.gpub.entity.Chercheur;
import com.example.gpub.entity.Publication;
import com.example.gpub.entity.PublicationCoauteur;
import com.example.gpub.repository.ChercheurRepository;
import com.example.gpub.repository.PublicationCoauteurRepository;
import com.example.gpub.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CoAuteurService {

    @Autowired
    private PublicationCoauteurRepository coauteurRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private ChercheurRepository chercheurRepository;

    @Autowired
    private NotificationService notificationService;

    public List<Map<String, Object>> getCoAuteurs(Long publicationId) {
        List<PublicationCoauteur> coauteurs = coauteurRepository.findByPublicationId(publicationId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (PublicationCoauteur pc : coauteurs) {
            Chercheur c = pc.getChercheur();
            Map<String, Object> coauteur = new LinkedHashMap<>();
            coauteur.put("id", c.getId());
            coauteur.put("nom", c.getNom());
            coauteur.put("email", c.getEmail());
            coauteur.put("domaine", c.getDomaine());
            coauteur.put("photoUrl", c.getPhotoUrl());
            result.add(coauteur);
        }
        return result;
    }

    public Map<String, Object> addCoAuteur(Long publicationId, Long chercheurId) {
        Publication publication = publicationRepository.findById(publicationId)
            .orElseThrow(() -> new RuntimeException("Publication not found with id: " + publicationId));

        Chercheur chercheur = chercheurRepository.findById(chercheurId)
            .orElseThrow(() -> new RuntimeException("Chercheur not found with id: " + chercheurId));

        if (coauteurRepository.existsByPublicationIdAndChercheurId(publicationId, chercheurId)) {
            throw new RuntimeException("Chercheur is already a co-author of this publication");
        }

        if (publication.getAuteurPrincipal() != null &&
            publication.getAuteurPrincipal().getId().equals(chercheurId)) {
            throw new RuntimeException("Chercheur is already the principal author of this publication");
        }

        PublicationCoauteur pc = new PublicationCoauteur();
        pc.setPublication(publication);
        pc.setChercheur(chercheur);
        coauteurRepository.save(pc);

        // ✅ Notify the co-author
        notificationService.createNotification(
            chercheurId,
            "COAUTEUR_AJOUTE",
            "Vous avez été ajouté comme co-auteur",
            "Vous avez été ajouté comme co-auteur de \"" + publication.getTitre() + "\"",
            "/publications/" + publicationId
        );

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Co-author added successfully");
        response.put("publicationId", publicationId);
        response.put("coAuteurId", chercheurId);
        response.put("coAuteurNom", chercheur.getNom());
        return response;
    }

    public Map<String, Object> removeCoAuteur(Long publicationId, Long chercheurId) {
        if (!coauteurRepository.existsByPublicationIdAndChercheurId(publicationId, chercheurId)) {
            throw new RuntimeException("Chercheur is not a co-author of this publication");
        }

        coauteurRepository.deleteByPublicationIdAndChercheurId(publicationId, chercheurId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Co-author removed successfully");
        response.put("publicationId", publicationId);
        response.put("coAuteurId", chercheurId);
        return response;
    }
}

