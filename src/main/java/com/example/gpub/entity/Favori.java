package com.example.gpub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "favori")
public class Favori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chercheur_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Chercheur chercheur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Publication publication;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Favori() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Chercheur getChercheur() { return chercheur; }
    public void setChercheur(Chercheur chercheur) { this.chercheur = chercheur; }
    public Publication getPublication() { return publication; }
    public void setPublication(Publication publication) { this.publication = publication; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}