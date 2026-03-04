package com.example.gpub.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chercheur_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Chercheur chercheur;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "titre", length = 255)
    private String titre;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "lien", length = 500)
    private String lien;

    @Column(name = "lu")
    private Boolean lu = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Notification() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Chercheur getChercheur() { return chercheur; }
    public void setChercheur(Chercheur chercheur) { this.chercheur = chercheur; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getLien() { return lien; }
    public void setLien(String lien) { this.lien = lien; }
    public Boolean getLu() { return lu; }
    public void setLu(Boolean lu) { this.lu = lu; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}