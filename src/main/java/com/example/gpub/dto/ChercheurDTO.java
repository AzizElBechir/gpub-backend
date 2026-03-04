package com.example.gpub.dto;

public class ChercheurDTO {
    private Long id;
    private String nom;
    private String email;
    private String password;  // ADDED THIS FIELD
    private String photoUrl;
    private String bio;
    private String domaine;
    private Long uniteId;
    private String uniteNom;
    
    // Constructors
    public ChercheurDTO() {}
    
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    // ADDED PASSWORD GETTER AND SETTER
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public Long getUniteId() {
        return uniteId;
    }
    
    public void setUniteId(Long uniteId) {
        this.uniteId = uniteId;
    }
    
    public String getUniteNom() {
        return uniteNom;
    }
    
    public void setUniteNom(String uniteNom) {
        this.uniteNom = uniteNom;
    }
}