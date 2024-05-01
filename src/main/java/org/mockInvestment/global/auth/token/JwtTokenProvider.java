package org.mockInvestment.global.auth.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.mockInvestment.auth.dto.AuthInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final Key signingKey;
    private final Long expireMilliseconds;


    public JwtTokenProvider(@Value("${spring.jwt.secretKey}") String secretKey,
                            @Value("${spring.jwt.expire-length.access}") Long expireMilliseconds) {
        this.expireMilliseconds = expireMilliseconds;
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(AuthInfo authInfo) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireMilliseconds);

        return Jwts.builder()
                .claim("id", authInfo.getId())
                .claim("username", authInfo.getUsername())
                .claim("role", authInfo.getRole())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(signingKey)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public AuthInfo getParsedClaims(String token) {
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {
            Long id = ex.getClaims().get("id", Long.class);
            String username = ex.getClaims().get("username", String.class);
            String role = ex.getClaims().get("role", String.class);
            return new AuthInfo(id, role, username);
        }

        Long id = claims.get("id", Long.class);
        String name = claims.get("name", String.class);
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);
        return new AuthInfo(id, role, username);
    }

}
