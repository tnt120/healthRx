package com.healthrx.backend.security.service;

import com.healthrx.backend.security.util.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
    public Claims extractAllClaims(String token, TokenType tokenType);
    public Header<?> extractAllHeaders(String token, TokenType tokenType);
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, TokenType tokenType);
    public String extractEmail(String token, TokenType tokenType);
    public Date extractExpiration(String token, TokenType tokenType);
    public String generateToken(UserDetails userDetails, TokenType tokenType);
    public boolean isTokenValid(String token, UserDetails userDetails);
}
