package com.nameless.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${application.security.jwt.accessSecret-key}")
  private String accessSecretKey;
  @Value("${application.security.jwt.refresh-token.refreshSecret-key}")
  private String refreshSecretKey;
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  public String extractUsernameFromAccess(String token) {
    return extractClaim(token, Claims::getSubject, accessSecretKey);
  }

  public String extractUsernameFromRefresh(String token) {
    return extractClaim(token, Claims::getSubject, refreshSecretKey);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String key) {
    final Claims claims = extractAllClaims(token, key);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return buildToken(extraClaims, userDetails, jwtExpiration, accessSecretKey);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration, refreshSecretKey);
  }

  private String buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          long expiration,
          String key
  ) {
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(key), SignatureAlgorithm.HS256)
            .compact();
  }

  public boolean isAccessTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsernameFromAccess(token);
    return (username.equals(userDetails.getUsername())) && !isAccessTokenExpired(token);
  }

  public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsernameFromRefresh(token);
    return (username.equals(userDetails.getUsername())) && !isRefreshTokenExpired(token);
  }

  public boolean isAccessTokenExpired(String token) {
    return extractExpiration(token, accessSecretKey).before(new Date());
  }

  public boolean isRefreshTokenExpired(String token) {
    return extractExpiration(token, refreshSecretKey).before(new Date());
  }

  private Date extractExpiration(String token, String key) {
    return extractClaim(token, Claims::getExpiration, key);
  }

  private Claims extractAllClaims(String token, String key) {
    return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey(key))
            .build()
            .parseClaimsJws(token)
            .getBody();
  }

  private Key getSignInKey(String secretKey) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}