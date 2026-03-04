package com.example.gpub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("GPUB - Plateforme de Publications Scientifiques")
                .version("1.0.0")
                .description("""
                    API backend pour la gestion des publications scientifiques des universités mauritaniennes.
                    
                    ## Rôles
                    - **USER** : Chercheur — gère ses publications et son profil
                    - **ADMIN** : Administrateur d'université — modère son établissement
                    - **SUPER_ADMIN** : Ministère — accès complet à toute la plateforme
                    
                    ## Authentification
                    Utilisez le endpoint `/api/auth/login` pour obtenir un token JWT.
                    Ajoutez-le dans le header : `Authorization: Bearer <token>`
                    """)
                .contact(new Contact()
                    .name("Ministère de l'Enseignement Supérieur et de la Recherche Scientifique")
                    .email("contact@ministere.mr")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Entrez votre token JWT")
                )
            );
    }
}