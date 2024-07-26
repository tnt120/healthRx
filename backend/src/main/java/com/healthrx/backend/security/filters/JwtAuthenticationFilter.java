package com.healthrx.backend.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthrx.backend.handler.BusinessErrorCodes;
import com.healthrx.backend.security.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.healthrx.backend.handler.BusinessErrorCodes.TOKEN_EXPIRED;
import static com.healthrx.backend.security.util.TokenType.ACCESS;
import static java.util.Objects.isNull;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getServletPath().contains("/api/auth/register") || request.getServletPath().contains("/api/auth/login") || request.getServletPath().contains("/api/auth/refresh")) {
            filterChain.doFilter(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();

        if (isNull(cookies)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("access_token"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        final String userEmail;

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            userEmail = jwtService.extractEmail(accessToken, ACCESS);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = this.userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(accessToken, user)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities()
                    );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

                filterChain.doFilter(request, response);
            }
        } catch (ExpiredJwtException e) {
            handleError(TOKEN_EXPIRED, response);
        }
    }

    private void handleError(BusinessErrorCodes error, HttpServletResponse response) throws IOException {
        response.resetBuffer();
        response.setStatus(error.getHttpStatus().value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(APPLICATION_JSON));
        response.getOutputStream().print(new ObjectMapper().writeValueAsString(error));
        response.flushBuffer();
    }
}
