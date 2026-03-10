package com.example.gpub.service;

import com.example.gpub.dto.ChercheurDTO;
import com.example.gpub.entity.Chercheur;
import com.example.gpub.entity.UniteRecherche;
import com.example.gpub.repository.ChercheurRepository;
import com.example.gpub.repository.UniteRechercheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

@Service
@Transactional
public class ChercheurService {
    
    @Autowired
    private ChercheurRepository chercheurRepository;
    
    @Autowired
    private UniteRechercheRepository uniteRechercheRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    
    public List<ChercheurDTO> getAllChercheurs() {
        return chercheurRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /** Recherche par nom ou email (paramètre optionnel : search, nom ou query) */
    public List<ChercheurDTO> searchChercheurs(String search) {
        if (search == null || search.trim().isEmpty()) {
            return getAllChercheurs();
        }
        String term = search.trim();
        return chercheurRepository
                .findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ChercheurDTO getChercheurById(Long id) {
        Chercheur chercheur = chercheurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chercheur not found with id: " + id));
        return convertToDTO(chercheur);
    }
    
    public ChercheurDTO createChercheur(ChercheurDTO chercheurDTO) {
        // Check if email already exists
        if (chercheurRepository.existsByEmail(chercheurDTO.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        String rawPassword = chercheurDTO.getPassword();
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new RuntimeException("Le mot de passe est obligatoire.");
        }
        if (rawPassword.length() < 8) {
            throw new RuntimeException("Le mot de passe doit contenir au moins 8 caractères.");
        }
        
        Chercheur chercheur = new Chercheur();
        chercheur.setNom(chercheurDTO.getNom());
        chercheur.setEmail(chercheurDTO.getEmail());
        chercheur.setBio(chercheurDTO.getBio());
        chercheur.setDomaine(chercheurDTO.getDomaine());
        chercheur.setPhotoUrl(chercheurDTO.getPhotoUrl());
        chercheur.setDateCreation(LocalDateTime.now());
        chercheur.setRole("USER");
        chercheur.setActif(true);
        chercheur.setHashMdp(passwordEncoder.encode(rawPassword.trim()));
        
        

        // Set UniteRecherche if provided
        if (chercheurDTO.getUniteId() != null) {
            UniteRecherche unite = uniteRechercheRepository.findById(chercheurDTO.getUniteId())
                .orElseThrow(() -> new RuntimeException("Unite Recherche not found with id: " + chercheurDTO.getUniteId()));
            chercheur.setUniteRecherche(unite);
        }
        
        Chercheur saved = chercheurRepository.save(chercheur);

        
        return convertToDTO(saved);
    }
    
    public ChercheurDTO updateChercheur(Long id, ChercheurDTO chercheurDTO) {
        Chercheur chercheur = chercheurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chercheur not found with id: " + id));
        
        if (!chercheur.getEmail().equals(chercheurDTO.getEmail())) {
            if (chercheurRepository.existsByEmail(chercheurDTO.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
        }
        
        chercheur.setNom(chercheurDTO.getNom());
        chercheur.setEmail(chercheurDTO.getEmail());
        chercheur.setBio(chercheurDTO.getBio());
        chercheur.setDomaine(chercheurDTO.getDomaine());
        chercheur.setPhotoUrl(chercheurDTO.getPhotoUrl());
        
        if (chercheurDTO.getPassword() != null && !chercheurDTO.getPassword().isEmpty()) {
            chercheur.setHashMdp(passwordEncoder.encode(chercheurDTO.getPassword()));
        }
        
        if (chercheurDTO.getUniteId() != null) {
            UniteRecherche unite = uniteRechercheRepository.findById(chercheurDTO.getUniteId())
                .orElseThrow(() -> new RuntimeException("Unite Recherche not found with id: " + chercheurDTO.getUniteId()));
            chercheur.setUniteRecherche(unite);
        }
        
        return convertToDTO(chercheurRepository.save(chercheur));
    }
    
    public void deleteChercheur(Long id) {
        if (!chercheurRepository.existsById(id)) {
            throw new RuntimeException("Chercheur not found with id: " + id);
        }
        chercheurRepository.deleteById(id);
    }
    
    private ChercheurDTO convertToDTO(Chercheur chercheur) {
        ChercheurDTO dto = new ChercheurDTO();
        dto.setId(chercheur.getId());
        dto.setNom(chercheur.getNom());
        dto.setEmail(chercheur.getEmail());
        dto.setPhotoUrl(chercheur.getPhotoUrl());
        dto.setBio(chercheur.getBio());
        dto.setDomaine(chercheur.getDomaine());
        
        if (chercheur.getUniteRecherche() != null) {
            dto.setUniteId(chercheur.getUniteRecherche().getId());
            dto.setUniteNom(chercheur.getUniteRecherche().getNom());
        }
        
        return dto;
    }
}
