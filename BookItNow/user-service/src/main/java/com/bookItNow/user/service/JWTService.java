package com.bookItNow.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secret}")
    private String secert;
    private SecretKey key;

    public String generateToken(String username, String role, int userId) {

        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("role",role);
        claims.put("userId",userId);

        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .claims()
                .id(jti)
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 3600*1000))
                .and()
                .signWith(getKey())
                .compact();


    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secert);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {

        return extractClaims(token, Claims::getSubject);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
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

    public int extractUserId(String token){
        int userId = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Integer.class);

        return userId;
    }

    public String extractJti(String token){
        return extractClaims(token, Claims::getId);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        final String role = extractRole(token);

        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && userDetails.getAuthorities().contains(new SimpleGrantedAuthority(role)));
    }

    public boolean isTokenExpired(String token) {
        return extractExpriation(token).before(new Date());
    }

    private Date extractExpriation(String token) {
        return extractClaims(token, Claims::getExpiration);
    }
}
