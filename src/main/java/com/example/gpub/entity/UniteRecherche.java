package com.example.gpub.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@Entity
@Table(name = "unite_recherche")
public class UniteRecherche {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nom", length = 255)
    private String nom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculte_id")
    @JsonIgnoreProperties({"unitesRecherche", "hibernateLazyInitializer", "handler"})
    private Faculte faculte;
    
    @OneToMany(mappedBy = "uniteRecherche")
    @JsonIgnoreProperties({"uniteRecherche", "hibernateLazyInitializer", "handler"})
    private List<Chercheur> chercheurs;
    
    // Constructors
    public UniteRecherche() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Faculte getFaculte() { return faculte; }
    public void setFaculte(Faculte faculte) { this.faculte = faculte; }
    public List<Chercheur> getChercheurs() { return chercheurs; }
    public void setChercheurs(List<Chercheur> chercheurs) { this.chercheurs = chercheurs; }
}
