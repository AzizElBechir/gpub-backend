package com.example.gpub.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chercheur")
public class Chercheur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nom", length = 150)
    private String nom;
    
    @Column(name = "email", length = 150)
    private String email;
    
    @Column(name = "hash_mdp", length = 255)
    private String hashMdp;
    
    @Column(name = "photo_url", columnDefinition = "TEXT")
    private String photoUrl;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "domaine", length = 150)
    private String domaine;
    
    // ADD THIS - Role field (default is USER since only chercheurs can register)
    @Column(name = "role", length = 20)
    private String role = "USER";

 
@Column(name = "actif")
private Boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_universite_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Universite adminUniversite;

    public Boolean getActif() { return actif; }
    public void setActif(Boolean actif) { this.actif = actif; }
    public Universite getAdminUniversite() { return adminUniversite; }
    public void setAdminUniversite(Universite adminUniversite) { this.adminUniversite = adminUniversite; }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unite_id")
    @JsonIgnoreProperties({"chercheurs", "faculte"})
    private UniteRecherche uniteRecherche;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @OneToMany(mappedBy = "chercheur")
    @JsonIgnoreProperties("chercheur")
    private List<PublicationCoauteur> publications;
    
    // Constructors
    public Chercheur() {}
    
    // All existing getters and setters...
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getHashMdp() {
        return hashMdp;
    }
    
    public void setHashMdp(String hashMdp) {
        this.hashMdp = hashMdp;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getDomaine() {
        return domaine;
    }
    
    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }
    
    // ADD THIS - Role getter and setter
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public UniteRecherche getUniteRecherche() {
        return uniteRecherche;
    }
    
    public void setUniteRecherche(UniteRecherche uniteRecherche) {
        this.uniteRecherche = uniteRecherche;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public List<PublicationCoauteur> getPublications() {
        return publications;
    }
    
    public void setPublications(List<PublicationCoauteur> publications) {
        this.publications = publications;
    }
}