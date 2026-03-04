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
    
    public List<Faculte> getFacultes() {
        return facultes;
    }
    
    public void setFacultes(List<Faculte> facultes) {
        this.facultes = facultes;
    }
}