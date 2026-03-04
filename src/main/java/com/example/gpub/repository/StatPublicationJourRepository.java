package com.example.gpub.repository;

import com.example.gpub.entity.StatPublicationJour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatPublicationJourRepository extends JpaRepository<StatPublicationJour, Long> {

    Optional<StatPublicationJour> findByPublicationIdAndJour(Long publicationId, LocalDate jour);
    List<StatPublicationJour> findByPublicationId(Long publicationId);
    List<StatPublicationJour> findByJour(LocalDate jour);

    @Query("SELECT s FROM StatPublicationJour s WHERE s.publication.id = :publicationId AND s.jour BETWEEN :dateDebut AND :dateFin")
    List<StatPublicationJour> findByPublicationIdAndJourBetween(
        @Param("publicationId") Long publicationId,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );

    @Query("SELECT s FROM StatPublicationJour s WHERE s.publication.id IN :publicationIds AND s.jour BETWEEN :dateDebut AND :dateFin")
    List<StatPublicationJour> findByPublicationIdInAndJourBetween(
        @Param("publicationIds") List<Long> publicationIds,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );
}