package com.healthrx.backend.security.service;

import com.healthrx.backend.security.util.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.healthrx.backend.security.util.TokenType.*;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${application.security.access-jwt.secret-key}")
    private String SECRET_ACCESS_KEY;

    @Value("${application.security.refresh-jwt.secret-key}")
    private String SECRET_REFRESH_KEY;

    @Value("${application.security.verification-jwt.secret-key}")
    private String SECRET_VERIFICATION_KEY;

    @Value("${application.security.access-jwt.expiration}")
    private long ACCESS_JWT_EXPIRATION;

    @Value("${application.security.refresh-jwt.expiration}")
    private long REFRESH_JWT_EXPIRATION;

    @Value("${application.security.verification-jwt.expiration}")
    private long VERIFICATION_JWT_EXPIRATION;

    private final RedisService redisService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Override
    public Claims extractAllClaims(String token, TokenType tokenType) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey(tokenType))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Header<?> extractAllHeaders(String token, TokenType tokenType) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey(tokenType))
                .build()
                .parseClaimsJws(token)
                .getHeader();
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, TokenType tokenType) {
        final Claims claims = extractAllClaims(token, tokenType);
        return claimsResolver.apply(claims);
    }

    @Override
    public String extractEmail(String token, TokenType tokenType) {
        return extractClaim(token, Claims::getSubject, tokenType);
    }

    @Override
    public Date extractExpiration(String token, TokenType tokenType) {
        return extractClaim(token, Claims::getExpiration, tokenType);
    }

    @Override
    public String generateToken(UserDetails userDetails, TokenType tokenType) {
        switch (tokenType) {
            case ACCESS -> {
                return Jwts.builder()
                        .setClaims(Map.of())
                        .setSubject(userDetails.getUsername())
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_JWT_EXPIRATION))
                        .signWith(getSigningKey(ACCESS), SignatureAlgorithm.HS256)
                        .compact();
            }
            case VERIFICATION -> {
                String verificationToken = Jwts.builder()
                        .setClaims(Map.of())
                        .setSubject(userDetails.getUsername())
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + VERIFICATION_JWT_EXPIRATION))
                        .signWith(getSigningKey(VERIFICATION), SignatureAlgorithm.HS256)
                        .compact();

                this.redisService.saveToken(userDetails.getUsername(), verificationToken, VERIFICATION);

                return verificationToken;
            }
            case REFRESH -> {
                Map<String, Object> headers = new HashMap<>();
                headers.put("source", getHeader("source"));
                headers.put("user-agent", getHeader("user-agent"));
                headers.put("origin", getHeader("origin"));
                headers.put("sec-chu-ua-mobile", getHeader("sec-ch-ua-mobile"));

                Date expiration = new Date(System.currentTimeMillis() + REFRESH_JWT_EXPIRATION);

                String refreshToken = Jwts.builder()
                        .setHeaderParams(headers)
                        .setSubject(userDetails.getUsername())
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(expiration)
                        .signWith(getSigningKey(REFRESH), SignatureAlgorithm.HS256)
                        .compact();

                this.redisService.saveToken(userDetails.getUsername(), refreshToken, REFRESH);

                return refreshToken;
            }
        }

        return null;
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token, ACCESS);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token, ACCESS);
    }

    @Override
    public boolean isTokenExpired(String token, TokenType tokenType) {
        return extractExpiration(token, tokenType).before(new Date());
    }

    private Key getSigningKey(TokenType tokenType) {
        String secret = switch (tokenType) {
            case ACCESS -> SECRET_ACCESS_KEY;
            case REFRESH -> SECRET_REFRESH_KEY;
            case VERIFICATION -> SECRET_VERIFICATION_KEY;
        };
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String getHeader(String headerName) {
        return httpServletRequest.getHeader(headerName);
    }
}
