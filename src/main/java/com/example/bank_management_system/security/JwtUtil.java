package com.example.bank_management_system.security;

import com.example.bank_management_system.config.JwtProperties;
import com.example.bank_management_system.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final JwtProperties jwtProperties;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    public void validateSecret() {
        if (jwtProperties.getSecret() == null || jwtProperties.getSecret().length() < 32) {
            throw new IllegalStateException(
                "jwt.secret must be at least 32 characters. Check application.properties."
            );
        }
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name())
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    public boolean validateToken(String token, String username) {
        try {
            Claims claims = getClaims(token);
            return claims.getSubject().equals(username)
                    && !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}