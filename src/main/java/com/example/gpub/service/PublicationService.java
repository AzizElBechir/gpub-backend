package com.example.gpub.service;

import com.example.gpub.dto.PublicationDTO;
import com.example.gpub.entity.Chercheur;
import com.example.gpub.entity.Publication;
import com.example.gpub.repository.ChercheurRepository;
import com.example.gpub.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PublicationService {
    
    @Autowired
    private PublicationRepository publicationRepository;
    
    @Autowired
    private ChercheurRepository chercheurRepository;

    public Page<PublicationDTO> getAllPublications(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return publicationRepository.findAll(pageable).map(this::convertToDTO);
    }
    
    // ✅ Only one searchPublications - uses published-only queries
    public Page<PublicationDTO> searchPublications(
            String query, String domaine, String auteur, Integer annee,
            int page, int size, String sortBy, String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        if ((query == null || query.isEmpty()) && 
            (domaine == null || domaine.isEmpty()) && 
            (auteur == null || auteur.isEmpty()) && 
            annee == null) {
            return publicationRepository.findAllPublishedByOrderByCreatedAtDesc(pageable)
                .map(this::convertToDTO);
        }
        
        return publicationRepository.advancedSearchPublished(
            query, domaine, auteur, annee, pageable
        ).map(this::convertToDTO);
    }
    
    public List<PublicationDTO> getPublicationsByAuteur(Long chercheurId) {
        return publicationRepository.findByAuteurPrincipalId(chercheurId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PublicationDTO getPublicationById(Long id) {
        Publication publication = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found with id: " + id));
        return convertToDTO(publication);
    }
    
    public PublicationDTO createPublication(PublicationDTO publicationDTO) {
        // ✅ Duplicate check
        if (publicationDTO.getDatePublication() != null && publicationDTO.getAuteurPrincipalId() != null) {
            int annee = publicationDTO.getDatePublication().getYear();
            boolean isDuplicate = publicationRepository.existsDuplicate(
                publicationDTO.getTitre(), annee, publicationDTO.getAuteurPrincipalId(), null
            );
            if (isDuplicate) {
                throw new RuntimeException(
                    "A publication with the same title, year and author already exists"
                );
            }
        }

        Publication publication = new Publication();
        publication.setTitre(publicationDTO.getTitre());
        publication.setResume(publicationDTO.getResume());
        publication.setMotsCles(publicationDTO.getMotsCles());
        publication.setDomaine(publicationDTO.getDomaine());
        publication.setDatePublication(publicationDTO.getDatePublication());
        publication.setPdfUrl(publicationDTO.getPdfUrl());
        publication.setAffiliationTexte(publicationDTO.getAffiliationTexte());
        publication.setCreatedAt(LocalDateTime.now());
        publication.setUpdatedAt(LocalDateTime.now());
        publication.setStatut("EN_ATTENTE");
        
        if (publicationDTO.getAuteurPrincipalId() != null) {
            Chercheur auteur = chercheurRepository.findById(publicationDTO.getAuteurPrincipalId())
                .orElseThrow(() -> new RuntimeException("Chercheur not found with id: " + publicationDTO.getAuteurPrincipalId()));
            publication.setAuteurPrincipal(auteur);
        }
        
        return convertToDTO(publicationRepository.save(publication));
    }
    
    public PublicationDTO updatePublication(Long id, PublicationDTO publicationDTO) {
        Publication existing = publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found with id: " + id));

        // ✅ Duplicate check (exclude current publication)
        if (publicationDTO.getDatePublication() != null) {
            int annee = publicationDTO.getDatePublication().getYear();
            Long auteurId = publicationDTO.getAuteurPrincipalId() != null
                ? publicationDTO.getAuteurPrincipalId()
                : existing.getAuteurPrincipal().getId();

            boolean isDuplicate = publicationRepository.existsDuplicate(
                publicationDTO.getTitre(), annee, auteurId, id
            );
            if (isDuplicate) {
                throw new RuntimeException(
                    "A publication with the same title, year and author already exists"
                );
            }
        }
        
        existing.setTitre(publicationDTO.getTitre());
        existing.setResume(publicationDTO.getResume());
        existing.setMotsCles(publicationDTO.getMotsCles());
        existing.setDomaine(publicationDTO.getDomaine());
        existing.setDatePublication(publicationDTO.getDatePublication());
        existing.setAffiliationTexte(publicationDTO.getAffiliationTexte());
        existing.setUpdatedAt(LocalDateTime.now());
        
        if (publicationDTO.getPdfUrl() != null) {
            existing.setPdfUrl(publicationDTO.getPdfUrl());
        }
        
        if (publicationDTO.getAuteurPrincipalId() != null) {
            Chercheur auteur = chercheurRepository.findById(publicationDTO.getAuteurPrincipalId())
                .orElseThrow(() -> new RuntimeException("Chercheur not found with id: " + publicationDTO.getAuteurPrincipalId()));
            existing.setAuteurPrincipal(auteur);
        }
        
        return convertToDTO(publicationRepository.save(existing));
    }
    
    public void deletePublication(Long id) {
        if (!publicationRepository.existsById(id)) {
            throw new RuntimeException("Publication not found with id: " + id);
        }
        publicationRepository.deleteById(id);
    }
    
    private PublicationDTO convertToDTO(Publication publication) {
        PublicationDTO dto = new PublicationDTO();
        dto.setId(publication.getId());
        dto.setTitre(publication.getTitre());
        dto.setResume(publication.getResume());
        dto.setMotsCles(publication.getMotsCles());
        dto.setDomaine(publication.getDomaine());
        dto.setDatePublication(publication.getDatePublication());
        dto.setPdfUrl(publication.getPdfUrl());
        dto.setAffiliationTexte(publication.getAffiliationTexte());
        dto.setStatut(publication.getStatut());
        
        if (publication.getAuteurPrincipal() != null) {
            dto.setAuteurPrincipalId(publication.getAuteurPrincipal().getId());
            dto.setAuteurPrincipalNom(publication.getAuteurPrincipal().getNom());
        }
        
        return dto;
    }
}