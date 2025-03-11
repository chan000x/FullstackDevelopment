package com.chandana.jwt;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTUtil {
    private static final String SECRET_KEY = "foobar_123456789_foobar_123456789_foobar_123456789_foobar_123456789";
    private static final String DEFAULT_ISSUER = "http://localhost:8080/api/v1/customers"; // Replace with your app's URL
    private static final long EXPIRATION_DAYS = 15;

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Subject cannot be null or blank");
        }
        if (claims == null) {
            throw new IllegalArgumentException("Claims cannot be null");
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(DEFAULT_ISSUER)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(EXPIRATION_DAYS, ChronoUnit.DAYS)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
