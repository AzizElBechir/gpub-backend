package com.example.gpub.dto;

public class LoginResponse {
    private Long id;
    private String nom;
    private String email;
    private String role;
    private String token; // We'll add JWT later, for now just a simple token
    
    // Constructors
    public LoginResponse() {}
    
    public LoginResponse(Long id, String nom, String email, String role, String token) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.role = role;
        this.token = token;
    }
    
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
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
}