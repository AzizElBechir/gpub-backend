package com.example.gpub.repository;

import com.example.gpub.entity.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
    
    // Existing methods
    List<Publication> findByAuteurPrincipalId(Long chercheurId);
    List<Publication> findByDomaineContaining(String domaine);
    
    // Search with pagination
    @Query("SELECT p FROM Publication p WHERE " +
           "LOWER(p.titre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.resume) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.motsCles) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.domaine) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Publication> searchPublications(@Param("query") String query, Pageable pageable);
    
    // Filter by domain
    Page<Publication> findByDomaineContainingIgnoreCase(String domaine, Pageable pageable);
    
    // Filter by year
    @Query("SELECT p FROM Publication p WHERE YEAR(p.datePublication) = :year")
    Page<Publication> findByYear(@Param("year") int year, Pageable pageable);
    
    // Filter by author
    @Query("SELECT p FROM Publication p " +
           "LEFT JOIN p.auteurPrincipal a " +
           "LEFT JOIN p.coauteurs c " +
           "WHERE LOWER(a.nom) LIKE LOWER(CONCAT('%', :auteur, '%')) " +
           "OR LOWER(c.chercheur.nom) LIKE LOWER(CONCAT('%', :auteur, '%'))")
    Page<Publication> findByAuteur(@Param("auteur") String auteur, Pageable pageable);
    
    // Advanced search with multiple filters
    @Query("SELECT DISTINCT p FROM Publication p " +
           "LEFT JOIN p.auteurPrincipal a " +
           "LEFT JOIN p.coauteurs c " +
           "WHERE (:query IS NULL OR " +
           "LOWER(p.titre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.resume) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.motsCles) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:domaine IS NULL OR LOWER(p.domaine) LIKE LOWER(CONCAT('%', :domaine, '%'))) " +
           "AND (:auteur IS NULL OR LOWER(a.nom) LIKE LOWER(CONCAT('%', :auteur, '%')) OR LOWER(c.chercheur.nom) LIKE LOWER(CONCAT('%', :auteur, '%'))) " +
           "AND (:annee IS NULL OR YEAR(p.datePublication) = :annee)")
    Page<Publication> advancedSearch(
        @Param("query") String query,
        @Param("domaine") String domaine,
        @Param("auteur") String auteur,
        @Param("annee") Integer annee,
        Pageable pageable
    );

    // Check for duplicate: same title + same year + same principal author
       @Query("SELECT COUNT(p) > 0 FROM Publication p WHERE " +
              "LOWER(TRIM(p.titre)) = LOWER(TRIM(:titre)) AND " +
              "YEAR(p.datePublication) = :annee AND " +
              "p.auteurPrincipal.id = :auteurId AND " +
              "(:excludeId IS NULL OR p.id != :excludeId)")
       boolean existsDuplicate(
       @Param("titre") String titre,
       @Param("annee") int annee,
       @Param("auteurId") Long auteurId,
       @Param("excludeId") Long excludeId
       );
       
       // Only return published publications for public search
       @Query("SELECT p FROM Publication p LEFT JOIN p.auteurPrincipal a WHERE " +
              "p.statut = 'PUBLIE' AND " +
              "(:query IS NULL OR :query = '' OR LOWER(p.titre) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.resume) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.motsCles) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.domaine) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
              "(:domaine IS NULL OR :domaine = '' OR LOWER(p.domaine) LIKE LOWER(CONCAT('%', :domaine, '%'))) AND " +
              "(:auteur IS NULL OR :auteur = '' OR LOWER(a.nom) LIKE LOWER(CONCAT('%', :auteur, '%'))) AND " +
              "(:annee IS NULL OR YEAR(p.datePublication) = :annee)")
       Page<Publication> advancedSearchPublished(
       @Param("query") String query,
       @Param("domaine") String domaine,
       @Param("auteur") String auteur,
       @Param("annee") Integer annee,
       Pageable pageable
       );

       @Query("SELECT p FROM Publication p WHERE p.statut = 'PUBLIE' ORDER BY p.createdAt DESC")
       Page<Publication> findAllPublishedByOrderByCreatedAtDesc(Pageable pageable);

    // Get recent publications
    Page<Publication> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Publication> findByAuteurPrincipal_UniteRecherche_Faculte_Universite_Id(Long universiteId);

    // My publications (for authenticated user)
    Page<Publication> findByAuteurPrincipalIdOrderByCreatedAtDesc(Long auteurPrincipalId, Pageable pageable);
    Page<Publication> findByAuteurPrincipalIdAndStatutOrderByCreatedAtDesc(Long auteurPrincipalId, String statut, Pageable pageable);
}