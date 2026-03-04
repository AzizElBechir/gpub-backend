package com.example.gpub.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;


@Entity
@Table(name = "faculte")
public class Faculte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nom", length = 255)
    private String nom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universite_id")
    @JsonIgnoreProperties({"facultes", "hibernateLazyInitializer", "handler"})
    private Universite universite;
    
    @OneToMany(mappedBy = "faculte")
    @JsonIgnoreProperties({"faculte", "hibernateLazyInitializer", "handler"})
    private List<UniteRecherche> unitesRecherche;
    
    // Constructors
    public Faculte() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public Universite getUniversite() {
        return universite;
    }
    
    public void setUniversite(Universite universite) {
        this.universite = universite;
    }
    
    public List<UniteRecherche> getUnitesRecherche() {
        return unitesRecherche;
    }
    
    public void setUnitesRecherche(List<UniteRecherche> unitesRecherche) {
        this.unitesRecherche = unitesRecherche;
    }
}