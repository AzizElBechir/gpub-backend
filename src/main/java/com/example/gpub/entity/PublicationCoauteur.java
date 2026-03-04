package com.example.gpub.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "publication_coauteur")
public class PublicationCoauteur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;
    
    @ManyToOne
    @JoinColumn(name = "chercheur_id")
    private Chercheur chercheur;
    
    // Constructors
    public PublicationCoauteur() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Publication getPublication() {
        return publication;
    }
    
    public void setPublication(Publication publication) {
        this.publication = publication;
    }
    
    public Chercheur getChercheur() {
        return chercheur;
    }
    
    public void setChercheur(Chercheur chercheur) {
        this.chercheur = chercheur;
    }
}