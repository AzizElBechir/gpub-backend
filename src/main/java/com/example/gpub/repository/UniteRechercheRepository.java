package com.example.gpub.repository;

import com.example.gpub.entity.UniteRecherche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniteRechercheRepository extends JpaRepository<UniteRecherche, Long> {
}