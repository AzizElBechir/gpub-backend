package com.example.gpub.config;

import com.example.gpub.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

 @Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    String path = request.getRequestURI();
    String method = request.getMethod();

    // Skip token processing for public GET endpoints
    boolean isPublicGet = method.equals("GET") && (
        path.equals("/api/health") ||
        path.startsWith("/api/facultes") ||
        path.startsWith("/api/universites") ||
        path.startsWith("/api/unites") ||
        path.startsWith("/api/auteurs") ||
        path.startsWith("/api/publications") ||
        path.startsWith("/api/chercheurs") ||
        path.startsWith("/api/stats/publication") ||
        path.startsWith("/api/stats/chercheur") ||
        path.startsWith("/api/files/download")
    );

    boolean isPublicPost = method.equals("POST") && (
        path.startsWith("/api/auth/login") ||
        path.startsWith("/api/auth/register")
    );

    String authHeader = request.getHeader("Authorization");

    // If public path and no token provided, just skip filter
    if ((isPublicGet || isPublicPost) && (authHeader == null || !authHeader.startsWith("Bearer "))) {
        filterChain.doFilter(request, response);
        return;
    }

    String token = null;
    String email = null;

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
        try {
            email = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            // Token invalide ou expiré
        }
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        if (jwtUtil.validateToken(token, email)) {
            Long userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            request.setAttribute("userId", userId);
            request.setAttribute("userEmail", email);
            request.setAttribute("userRole", role);
        }
    }

    filterChain.doFilter(request, response);
}
}