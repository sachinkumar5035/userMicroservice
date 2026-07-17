package com.microservice.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long jwtExpirationMs; // in milliseconds

    public JwtUtil(@Value("${app.jwt.secret:defaultsecretkeychangeme}") String secret,
                   @Value("${app.jwt.expiration-ms:600000}") long jwtExpirationMs) {
        Key generatedKey;
        try {
            generatedKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            // fallback to a secure random key if provided secret is too weak
            generatedKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            System.out.println("[WARN] Provided JWT secret is too weak; using a generated key. Set 'app.jwt.secret' to a 32+ byte value to use a fixed secret.");
        }
        this.key = generatedKey;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    public String generateToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}

