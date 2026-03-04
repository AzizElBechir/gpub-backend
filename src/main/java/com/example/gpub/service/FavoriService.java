package com.example.gpub.service;

import com.example.gpub.entity.Chercheur;
import com.example.gpub.entity.Favori;
import com.example.gpub.entity.Publication;
import com.example.gpub.repository.ChercheurRepository;
import com.example.gpub.repository.FavoriRepository;
import com.example.gpub.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class FavoriService {

    @Autowired
    private FavoriRepository favoriRepository;

    @Autowired
    private ChercheurRepository chercheurRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    public List<Map<String, Object>> getFavoris(Long chercheurId) {
        List<Favori> favoris = favoriRepository.findByChercheurId(chercheurId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Favori f : favoris) {
            Publication pub = f.getPublication();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("favoriId", f.getId());
            item.put("addedAt", f.getCreatedAt());
            item.put("publicationId", pub.getId());
            item.put("titre", pub.getTitre());
            item.put("domaine", pub.getDomaine());
            item.put("statut", pub.getStatut());
            item.put("datePublication", pub.getDatePublication());
            item.put("pdfUrl", pub.getPdfUrl());
            if (pub.getAuteurPrincipal() != null) {
                item.put("auteurNom", pub.getAuteurPrincipal().getNom());
                item.put("auteurId", pub.getAuteurPrincipal().getId());
            }
            result.add(item);
        }
        return result;
    }

    public Map<String, Object> addFavori(Long chercheurId, Long publicationId) {
        if (favoriRepository.existsByChercheurIdAndPublicationId(chercheurId, publicationId)) {
            throw new RuntimeException("Publication already in favorites");
        }

        Chercheur chercheur = chercheurRepository.findById(chercheurId)
            .orElseThrow(() -> new RuntimeException("Chercheur not found"));

        Publication publication = publicationRepository.findById(publicationId)
            .orElseThrow(() -> new RuntimeException("Publication not found"));

        Favori favori = new Favori();
        favori.setChercheur(chercheur);
        favori.setPublication(publication);
        favori.setCreatedAt(LocalDateTime.now());
        favoriRepository.save(favori);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Publication added to favorites");
        response.put("publicationId", publicationId);
        response.put("titre", publication.getTitre());
        return response;
    }

    public Map<String, Object> removeFavori(Long chercheurId, Long publicationId) {
        if (!favoriRepository.existsByChercheurIdAndPublicationId(chercheurId, publicationId)) {
            throw new RuntimeException("Publication not in favorites");
        }

        favoriRepository.deleteByChercheurIdAndPublicationId(chercheurId, publicationId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Publication removed from favorites");
        response.put("publicationId", publicationId);
        return response;
    }

    public Map<String, Object> isFavori(Long chercheurId, Long publicationId) {
        boolean isFav = favoriRepository.existsByChercheurIdAndPublicationId(chercheurId, publicationId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("publicationId", publicationId);
        response.put("isFavori", isFav);
        return response;
    }
}