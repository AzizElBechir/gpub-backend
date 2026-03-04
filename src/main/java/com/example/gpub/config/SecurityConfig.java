package com.example.gpub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // Auth endpoints (public)
            .requestMatchers("/api/auth/**").permitAll()
            
            .requestMatchers(HttpMethod.GET, "/api/facultes/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/universites/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/unites/**").permitAll()

            // Admin endpoints
            .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
            .requestMatchers("/api/super-admin/**").hasAuthority("ROLE_SUPER_ADMIN")

            // Co-authors - GET is public, POST/DELETE require auth
            .requestMatchers(HttpMethod.GET, "/api/publications/*/coauteurs").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/publications/*/coauteurs/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/publications/*/coauteurs/**").authenticated()

            // Stats endpoints - MUST BE BEFORE GENERIC PATTERNS
            .requestMatchers("/api/stats/publication/**").permitAll()
            .requestMatchers("/api/stats/chercheur/**").permitAll()
            .requestMatchers("/api/stats/me").authenticated()
            
            .requestMatchers("/api/stats/me/**").authenticated()


            
            .requestMatchers("/api/me/**").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/publications/*/favori").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/publications/*/favori").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/publications/*/favori").authenticated()

            // Public export
            .requestMatchers(HttpMethod.GET, "/api/publications/export").permitAll()

            // Public GET endpoints for visitors
            .requestMatchers(HttpMethod.GET, "/api/publications/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/chercheurs/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/facultes/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/universites/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/unites/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/auteurs/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/files/download/**").permitAll()
            
            // File endpoints
            .requestMatchers("/api/files/upload").authenticated()
            .requestMatchers("/api/files/delete").authenticated()
            
            
            // Protected POST/PUT/DELETE endpoints (require authentication)
            .requestMatchers(HttpMethod.POST, "/api/**").authenticated()
            .requestMatchers(HttpMethod.PUT, "/api/**").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/**").authenticated()
            

            // Swagger UI
            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/v3/api-docs"
            ).permitAll()
            // All other requests require authentication
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}