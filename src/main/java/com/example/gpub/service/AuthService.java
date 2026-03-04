package com.example.gpub.service;

import com.example.gpub.dto.LoginRequest;
import com.example.gpub.dto.LoginResponse;
import com.example.gpub.entity.Chercheur;
import com.example.gpub.repository.ChercheurRepository;
import com.example.gpub.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    @Autowired
    private ChercheurRepository chercheurRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public LoginResponse login(LoginRequest loginRequest) {
        Chercheur chercheur = chercheurRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), chercheur.getHashMdp())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (chercheur.getActif() != null && !chercheur.getActif()) {
            throw new RuntimeException("ACCOUNT_DISABLED");
        }
        
        String token = jwtUtil.generateToken(
            chercheur.getId(), 
            chercheur.getEmail(), 
            chercheur.getRole()
        );
        
        return new LoginResponse(
            chercheur.getId(),
            chercheur.getNom(),
            chercheur.getEmail(),
            chercheur.getRole(),
            token
        );
    }
}