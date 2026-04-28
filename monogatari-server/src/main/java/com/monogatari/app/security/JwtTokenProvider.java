package com.monogatari.app.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
	@Value("${jwt.secret}")
	private String jwtSecret;
	
	@Value("${jwt.expiration}")
	private long jwtExpirationDate;
	
	private SecretKey key() {
		return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateToken(Authentication authentication) {
		String email = authentication.getName();
		Instant now = Instant.now();
		Instant expiry = now.plusMillis(jwtExpirationDate);

		List<String> roles = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return Jwts.builder()
				.subject(email)
				.claim("roles", roles)
				.issuedAt(Date.from(now))
				.expiration(Date.from(expiry))
				.signWith(key())
				.compact();
	}
	
	public String generateToken(String email) {
	   Instant now = Instant.now();
	   Instant expiry = now.plusMillis(jwtExpirationDate);
	   return Jwts.builder()
			 .subject(email)
			 .issuedAt(Date.from(now))
			 .expiration(Date.from(expiry))
			 .signWith(key())
			 .compact();
	}
	
	public String getEmail(String token) {
		return Jwts.parser()
				.verifyWith(key())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key()).build().parse(token);
			return true;
		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getRolesFromToken(String token) {
	    return (List<String>) Jwts.parser()
	            .verifyWith(key())
	            .build()
	            .parseSignedClaims(token)
	            .getPayload()
	            .get("roles"); 
	}
}