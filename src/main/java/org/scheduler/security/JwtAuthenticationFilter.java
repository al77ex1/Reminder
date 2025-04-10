package org.scheduler.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            log.debug("JWT from request: {}", jwt != null ? "present" : "null");

            if (StringUtils.hasText(jwt) && validateToken(jwt)) {
                Claims claims = extractClaims(jwt);
                String userId = claims.getSubject();
                
                List<SimpleGrantedAuthority> authorities = getAuthoritiesFromClaims(claims);
                
                setAuthentication(userId, authorities);
            } else {
                log.debug("No valid JWT token found in request");
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
        log.debug("Set authentication in SecurityContext: {}", authentication);
    }
    
    private List<SimpleGrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        log.debug("JWT claims: {}", claims);
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // Сначала пробуем извлечь разрешения
        authorities.addAll(extractPermissionsFromClaims(claims));
        
        // Если разрешений нет, пробуем использовать роли
        if (authorities.isEmpty()) {
            authorities.addAll(extractRolesFromClaims(claims));
        }
        
        log.debug("Final authorities: {}", authorities);
        return authorities;
    }
    
    private List<SimpleGrantedAuthority> extractPermissionsFromClaims(Claims claims) {
        if (!claims.containsKey("permissions")) {
            log.warn("No permissions claim found in JWT");
            return Collections.emptyList();
        }
        
        log.debug("Found permissions claim in JWT");
        try {
            @SuppressWarnings("unchecked")
            List<String> permissions = claims.get("permissions", List.class);
            if (permissions == null || permissions.isEmpty()) {
                log.warn("Permissions list is empty or null");
                return Collections.emptyList();
            }
            
            log.debug("Permissions from JWT: {}", permissions);
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
            log.debug("No roles claim found in JWT");
            return Collections.emptyList();
        }
        
        log.debug("No permissions found, trying to use roles");
        try {
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            if (roles == null || roles.isEmpty()) {
                log.debug("Roles list is empty or null");
                return Collections.emptyList();
            }
            
            log.debug("Roles from JWT: {}", roles);
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
            Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT token validation failed", e);
        }
        return false;
    }
}
