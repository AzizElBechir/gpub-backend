package com.example.gpub.repository;

import com.example.gpub.entity.Faculte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaculteRepository extends JpaRepository<Faculte, Long> {
}