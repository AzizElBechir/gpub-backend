package com.example.gpub.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "universite")
public class Universite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nom", length = 100)
    private String nom;
    
    @Column(name = "ville", length = 80)
    private String ville;
    
    @Column(name = "email", length = 120)
    private String email;
    
    @OneToMany(mappedBy = "universite")
    private List<Faculte> facultes;
    
    // Constructors
    public Universite() {}
    
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
    
    public String getVille() {
        return ville;
    }
    
    public void setVille(String ville) {
        this.ville = ville;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<Faculte> getFacultes() {
        return facultes;
    }
    
    public void setFacultes(List<Faculte> facultes) {
        this.facultes = facultes;
    }
}