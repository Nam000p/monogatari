package com.monogatari.app.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monogatari.app.enums.UserStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserDetailsService userDetailsService;

    private final ObjectMapper objectMapper;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = getJwtFromRequest(request);
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getEmail(token);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (userDetails == null) {
                    filterChain.doFilter(request, response);
                    return;
                }
                if (!userDetails.isEnabled()) {
                    handleErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, UserStatus.UNVERIFIED.name(),
                            "Please verify your email address.");
                    return;
                }
                if (!userDetails.isAccountNonLocked()) {
                    handleErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "LOCKED",
                            "Your account has been locked.");
                    return;
                }
                Collection<? extends GrantedAuthority> authorities;
                try {
                    List<String> roles = jwtTokenProvider.getRolesFromToken(token);
                    if (roles != null && !roles.isEmpty()) {
                        authorities = roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());
                    } else {
                        authorities = userDetails.getAuthorities();
                    }
                } catch (Exception e) {
                    log.warn("Could not extract roles from token, falling back to database authorities");
                    authorities = userDetails.getAuthorities();
                }
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT Authentication Filter Error: ", e);
            resolver.resolveException(request, response, null, e);
        }
    }

    private void handleErrorResponse(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        Map<String, String> data = new HashMap<>();
        data.put("error", error);
        data.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(data));
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}