package com.bookItNow.event.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.DefaultMessageCodesResolver;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    private String key;

    private SecretKey getKey(){
        // Use standard java.util.Base64
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver){
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractRole(String token) {
        String role = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class); // Extract role from token

        // Add the "ROLE_" prefix if it is not already present
        if (role != null && !role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        return role;
    }

    public String extractJti(String token){
        return extractClaims(token, Claims::getId);
    }

    public int extractUserId(String token){
        int userId = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Integer.class);

        return userId;
    }

    public boolean validateToken(String token) {
        try {
            // 1. Check if the token is expired
            boolean isExpired = extractClaims(token, Claims::getExpiration).before(new Date());

            // 2. The very act of parsing the claims verifies the signature
            // If the signature is wrong, an exception is thrown before it returns true
            return !isExpired;

        } catch (Exception e) {
            // Includes SignatureException, MalformedJwtException, etc.
            return false;
        }
    }
}
