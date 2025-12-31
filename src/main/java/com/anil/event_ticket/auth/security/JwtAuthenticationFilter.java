package com.anil.event_ticket.auth.security;

import com.anil.event_ticket.auth.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * ELITE PERFORMANCE FILTER
 * Adheres to Canon Performance Constraints: No N+1 queries.
 * Trust the JWT claims to avoid Database I/O on every request.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Extract Authorization header
        final String authHeader = request.getHeader("Authorization");

        // 2. Fast path: no token provided or wrong format
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract JWT from "Bearer <token>"
        final String jwt = authHeader.substring(7);

        try {
            // 4. Extract email from token
            final String userEmail = jwtService.extractEmail(jwt);

            // 5. Authenticate if email exists and context is empty
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 6. Signature & Expiry check (No DB hit)
                if (jwtService.isTokenValid(jwt)) {

                    // 7. 💎 ELITE: Extract roles directly from the token claims
                    List<SimpleGrantedAuthority> authorities = jwtService.extractAuthorities(jwt);

                    // 8. Create lightweight Principal (String email)
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userEmail,
                            null,
                            authorities
                    );

                    // 9. Attach metadata (IP, Session ID)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 10. Seal the context
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("User [{}] authenticated via JWT claims.", userEmail);
                }
            }

        } catch (ExpiredJwtException e) {
            log.warn("JWT Expired for request {}: {}", request.getRequestURI(), e.getMessage());
            // Pro Tip: You could add a custom header here like:
            // response.setHeader("Token-Status", "Expired");
        } catch (Exception e) {
            log.error("JWT Authentication processing failed", e);
            // Security Context remains empty; SecurityConfig handles the 401/403
        }

        // 11. Move to next filter in the chain
        filterChain.doFilter(request, response);
    }
}