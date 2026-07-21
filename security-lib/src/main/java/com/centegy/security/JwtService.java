package com.centegy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

public class JwtService {

    private final String secretKey;

    public JwtService(String secretKey) {
        this.secretKey = secretKey;
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username, Long userId, List<String> roles) {

        return Jwts.builder()
                .subject(username)
                .claim("id", userId)
                .claim("roles", roles)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 5 ))
                .signWith(getSecretKey())
                .compact();

    }

    public String generateRefreshToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60))
                .signWith(getSecretKey())
                .compact();

    }

    public String getUsername(String token){

        return getAllClaims(token).getSubject();

    }

    private Date getExpiration(String token){

        return getAllClaims(token).getExpiration();

    }


    public boolean isTokenValid(Claims claims, String username) {

        String extractedUsername = claims.getSubject();
        Date expiration = claims.getExpiration();
        return (username.equals(extractedUsername) && !expiration.before(new Date()));

    }

    private boolean isTokenExpired(String token) {

        return getExpiration(token).before(new Date());

    }

    public List<String> getRoles(Claims claims) {

        return claims.get("roles", List.class);
    }

    public Claims getAllClaims(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims;

    }





}
