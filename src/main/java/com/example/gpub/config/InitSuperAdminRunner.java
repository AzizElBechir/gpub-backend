package com.example.gpub.config;

import com.example.gpub.entity.Chercheur;
import com.example.gpub.repository.ChercheurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Crée le compte Super Admin au premier démarrage (profil dev) si aucun utilisateur avec cet email n'existe.
 * Mot de passe : admin123
 */
@Configuration
@Profile("dev")
public class InitSuperAdminRunner {

    @Autowired
    private ChercheurRepository chercheurRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String SUPER_ADMIN_EMAIL = "superadmin@ministere.mr";
    private static final String SUPER_ADMIN_PASSWORD = "admin123";

    @Bean
    public CommandLineRunner createSuperAdminIfMissing() {
        return args -> {
            if (chercheurRepository.existsByEmail(SUPER_ADMIN_EMAIL)) {
                return;
            }
            Chercheur admin = new Chercheur();
            admin.setNom("Super Admin");
            admin.setEmail(SUPER_ADMIN_EMAIL);
            admin.setHashMdp(passwordEncoder.encode(SUPER_ADMIN_PASSWORD));
            admin.setRole("SUPER_ADMIN");
            admin.setActif(true);
            admin.setDateCreation(java.time.LocalDateTime.now());
            chercheurRepository.save(admin);
            System.out.println("[GPUB] Compte Super Admin créé : " + SUPER_ADMIN_EMAIL + " / " + SUPER_ADMIN_PASSWORD);
        };
    }
}
