package org.scheduler.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scheduler.entity.Permission;
import org.scheduler.entity.Role;
import org.scheduler.entity.User;
import org.scheduler.exception.ApplicationRuntimeException;
import org.scheduler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    
    private final UserRepository userRepository;
    
    @Value("${auth.jwt.secret}")
    private String jwtSecret;
    
    @Value("${auth.jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${auth.refresh.expiration}")
    private long refreshExpiration;
    
    @Value("${auth.link.expiration}")
    private long authLinkExpiration;
    
    @Value("${auth.frontend.url}")
    private String frontendUrl;
    
    private final Map<String, AuthTokenInfo> oneTimeTokens = new ConcurrentHashMap<>();
    
    public String generateAuthLinkByUserId(Long telegramUserId) {
        boolean userExists = userRepository.existsByTelegramUserId(telegramUserId);
        
        if (userExists) {
            User user = userRepository.findByTelegramUserId(telegramUserId)
                    .orElseThrow(() -> new ApplicationRuntimeException("User not found"));
            String oneTimeToken = generateOneTimeToken(user.getTelegramUserName());
            return frontendUrl + "/auth?token=" + oneTimeToken;
        } else {
            return "Пользователь не зарегистрирован. Сначала зарегистрируйтесь.";
        }
    }
    
    private String generateOneTimeToken(String telegramUserName) {
        String token = UUID.randomUUID().toString();
        Date expiryDate = Date.from(
            LocalDateTime.now().plusSeconds(authLinkExpiration)
                .atZone(ZoneId.systemDefault()).toInstant());
        
        oneTimeTokens.put(token, new AuthTokenInfo(telegramUserName, expiryDate));
        
        log.info("Generated one-time token for user: {}", telegramUserName);
        return token;
    }
    
    public Map<String, String> validateOneTimeTokenAndGenerateJwt(String oneTimeToken) {
        AuthTokenInfo tokenInfo = oneTimeTokens.get(oneTimeToken);
        
        if (tokenInfo == null) {
            log.warn("One-time token not found: {}", oneTimeToken);
            throw new ApplicationRuntimeException("Invalid or expired token");
        }
        
        if (tokenInfo.getExpiryDate().before(new Date())) {
            log.warn("One-time token expired: {}", oneTimeToken);
            oneTimeTokens.remove(oneTimeToken);
            throw new ApplicationRuntimeException("Token expired");
        }
        
        User user = userRepository.findByTelegramUserName(tokenInfo.getTelegramUserName())
                .orElseThrow(() -> new ApplicationRuntimeException("User not found"));
        
        oneTimeTokens.remove(oneTimeToken);
        
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        
        log.info("Generated JWT tokens for user: {}", user.getTelegramUserName());
        return tokens;
    }
    
    private String generateAccessToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);
        
        List<String> roleNames = getRoleNames(user);
        List<String> permissionNames = getPermissionNames(user);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("username", user.getTelegramUserName())
                .claim("roles", roleNames)
                .claim("permissions", permissionNames)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    private String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration * 1000);
        
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    private List<String> getRoleNames(User user) {
        List<String> roleNames = new ArrayList<>();
        user.getRoles().forEach(role -> roleNames.add(role.getName().toString()));
        return roleNames;
    }
    
    private List<String> getPermissionNames(User user) {
        Set<Permission> permissions = new HashSet<>();
        
        for (Role role : user.getRoles()) {
            log.info("Processing role: {}", role.getName());
            permissions.addAll(role.getPermissions());
        }
        
        return permissions.stream()
                .map(Permission::getName)
                .toList();
    }
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
    
    private static class AuthTokenInfo {
        private final String telegramUserName;
        private final Date expiryDate;
        
        public AuthTokenInfo(String telegramUserName, Date expiryDate) {
            this.telegramUserName = telegramUserName;
            this.expiryDate = expiryDate;
        }
        
        public String getTelegramUserName() {
            return telegramUserName;
        }
        
        public Date getExpiryDate() {
            return expiryDate;
        }
    }
}
