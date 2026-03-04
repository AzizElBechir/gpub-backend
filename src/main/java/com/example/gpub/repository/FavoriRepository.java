package com.example.gpub.repository;

import com.example.gpub.entity.Favori;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriRepository extends JpaRepository<Favori, Long> {
    List<Favori> findByChercheurId(Long chercheurId);
    Optional<Favori> findByChercheurIdAndPublicationId(Long chercheurId, Long publicationId);
    boolean existsByChercheurIdAndPublicationId(Long chercheurId, Long publicationId);
    void deleteByChercheurIdAndPublicationId(Long chercheurId, Long publicationId);
}