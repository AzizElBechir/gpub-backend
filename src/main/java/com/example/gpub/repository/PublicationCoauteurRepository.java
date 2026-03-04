package com.example.gpub.repository;

import com.example.gpub.entity.PublicationCoauteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PublicationCoauteurRepository extends JpaRepository<PublicationCoauteur, Long> {
    List<PublicationCoauteur> findByPublicationId(Long publicationId);
    Optional<PublicationCoauteur> findByPublicationIdAndChercheurId(Long publicationId, Long chercheurId);
    boolean existsByPublicationIdAndChercheurId(Long publicationId, Long chercheurId);
    void deleteByPublicationIdAndChercheurId(Long publicationId, Long chercheurId);
}