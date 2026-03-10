package com.example.gpub.repository;

import com.example.gpub.entity.Chercheur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChercheurRepository extends JpaRepository<Chercheur, Long> {
    Optional<Chercheur> findByEmail(String email);
    boolean existsByEmail(String email);
    
    // Find chercheurs by their university (through unite -> faculte -> universite)
    List<Chercheur> findByUniteRecherche_Faculte_Universite_Id(Long universiteId);

    /** Recherche par nom ou email (contient, insensible à la casse) */
    List<Chercheur> findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(String nom, String email);
}