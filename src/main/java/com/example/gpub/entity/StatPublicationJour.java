package com.example.gpub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "stat_publication_jour")
public class StatPublicationJour {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;
    
    @Column(name = "jour")
    private LocalDate jour;
    
    @Column(name = "vues")
    private Integer vues;
    
    @Column(name = "telechargements")
    private Integer telechargements;
    
    // Constructors
    public StatPublicationJour() {}
    
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
    
    public LocalDate getJour() {
        return jour;
    }
    
    public void setJour(LocalDate jour) {
        this.jour = jour;
    }
    
    public Integer getVues() {
        return vues;
    }
    
    public void setVues(Integer vues) {
        this.vues = vues;
    }
    
    public Integer getTelechargements() {
        return telechargements;
    }
    
    public void setTelechargements(Integer telechargements) {
        this.telechargements = telechargements;
    }
}