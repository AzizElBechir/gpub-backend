package com.example.gpub.dto;

import java.time.LocalDate;
import java.util.List;

public class PublicationDTO {
    private Long id;
    private String titre;
    private String resume;
    private String motsCles;
    private String domaine;
    private LocalDate datePublication;
    private String pdfUrl;
    private String affiliationTexte;
    private Long auteurPrincipalId;
    private String auteurPrincipalNom;
    private List<ChercheurDTO> coauteurs;
    private String statut;

    // Constructors
    public PublicationDTO() {}
    
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
    
    public Long getAuteurPrincipalId() {
        return auteurPrincipalId;
    }
    
    public void setAuteurPrincipalId(Long auteurPrincipalId) {
        this.auteurPrincipalId = auteurPrincipalId;
    }
    
    public String getAuteurPrincipalNom() {
        return auteurPrincipalNom;
    }
    
    public void setAuteurPrincipalNom(String auteurPrincipalNom) {
        this.auteurPrincipalNom = auteurPrincipalNom;
    }
    
    public List<ChercheurDTO> getCoauteurs() {
        return coauteurs;
    }
    
    public void setCoauteurs(List<ChercheurDTO> coauteurs) {
        this.coauteurs = coauteurs;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}