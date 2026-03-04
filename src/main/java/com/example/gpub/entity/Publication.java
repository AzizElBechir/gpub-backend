package com.example.gpub.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "publication")
public class Publication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "titre", length = 255)
    private String titre;
    
    @Column(name = "resume", columnDefinition = "TEXT")
    private String resume;
    
    @Column(name = "mots_cles", columnDefinition = "TEXT")
    private String motsCles;
    
    @Column(name = "domaine", length = 255)
    private String domaine;
    
    @Column(name = "date_publication")
    private LocalDate datePublication;
    
    @Column(name = "pdf_url", columnDefinition = "TEXT")
    private String pdfUrl;
    
    @Column(name = "affiliation_texte", length = 255)
    private String affiliationTexte;
    
    @ManyToOne
    @JoinColumn(name = "auteur_principal_id")
    private Chercheur auteurPrincipal;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "publication")
    private List<PublicationCoauteur> coauteurs;
    
    @OneToMany(mappedBy = "publication")
    private List<StatPublicationJour> statistiques;
    
    @Column(name = "statut", length = 20)
    private String statut = "EN_ATTENTE";

    // Getter and Setter
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    // Constructors
    public Publication() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public String getResume() {
        return resume;
    }
    
    public void setResume(String resume) {
        this.resume = resume;
    }
    
    public String getMotsCles() {
        return motsCles;
    }
    
    public void setMotsCles(String motsCles) {
        this.motsCles = motsCles;
    }
    
    public String getDomaine() {
        return domaine;
    }
    
    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }
    
    public LocalDate getDatePublication() {
        return datePublication;
    }
    
    public void setDatePublication(LocalDate datePublication) {
        this.datePublication = datePublication;
    }
    
    public String getPdfUrl() {
        return pdfUrl;
    }
    
    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
    
    public String getAffiliationTexte() {
        return affiliationTexte;
    }
    
    public void setAffiliationTexte(String affiliationTexte) {
        this.affiliationTexte = affiliationTexte;
    }
    
    public Chercheur getAuteurPrincipal() {
        return auteurPrincipal;
    }
    
    public void setAuteurPrincipal(Chercheur auteurPrincipal) {
        this.auteurPrincipal = auteurPrincipal;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<PublicationCoauteur> getCoauteurs() {
        return coauteurs;
    }
    
    public void setCoauteurs(List<PublicationCoauteur> coauteurs) {
        this.coauteurs = coauteurs;
    }
    
    public List<StatPublicationJour> getStatistiques() {
        return statistiques;
    }
    
    public void setStatistiques(List<StatPublicationJour> statistiques) {
        this.statistiques = statistiques;
    }
}