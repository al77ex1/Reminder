package org.scheduler.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            log.info("JWT from request: {}", jwt != null ? "present" : "null");

            if (StringUtils.hasText(jwt) && validateToken(jwt)) {
                Claims claims = extractClaims(jwt);
                String userId = claims.getSubject();
                
                List<SimpleGrantedAuthority> authorities = getAuthoritiesFromClaims(claims);
                
                setAuthentication(userId, authorities);
            } else {
                log.info("No valid JWT token found in request");
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }
    
    private Claims extractClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret.getBytes())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
    
    private void setAuthentication(String userId, List<SimpleGrantedAuthority> authorities) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User authenticated with ID: {}", userId);
    }
    
    private List<SimpleGrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        // Сначала пробуем получить разрешения
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(extractPermissionsFromClaims(claims));
        
        // Если разрешений нет, пробуем получить роли
        if (authorities.isEmpty()) {
            authorities = new ArrayList<>(extractRolesFromClaims(claims));
        }
        
        return authorities;
    }
    
    private List<SimpleGrantedAuthority> extractPermissionsFromClaims(Claims claims) {
        if (!claims.containsKey("permissions")) {
            log.warn("No permissions claim found in JWT");
            return Collections.emptyList();
        }
        
        log.info("Found permissions claim in JWT");
        try {
            @SuppressWarnings("unchecked")
            List<String> permissions = claims.get("permissions", List.class);
            if (permissions == null || permissions.isEmpty()) {
                log.warn("Permissions list is empty or null");
                return Collections.emptyList();
            }
            
            log.info("Permissions from JWT: {}", permissions);
            return permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        } catch (Exception e) {
            log.error("Error extracting permissions from JWT", e);
            return Collections.emptyList();
        }
    }
    
    private List<SimpleGrantedAuthority> extractRolesFromClaims(Claims claims) {
        if (!claims.containsKey("roles")) {
            log.info("No roles claim found in JWT");
            return Collections.emptyList();
        }
        
        log.info("No permissions found, trying to use roles");
        try {
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            if (roles == null || roles.isEmpty()) {
                log.info("Roles list is empty or null");
                return Collections.emptyList();
            }
            
            log.info("Roles from JWT: {}", roles);
            // Добавляем роли как разрешения с префиксом ROLE_
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
        } catch (Exception e) {
            log.error("Error extracting roles from JWT", e);
            return Collections.emptyList();
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret.getBytes()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
