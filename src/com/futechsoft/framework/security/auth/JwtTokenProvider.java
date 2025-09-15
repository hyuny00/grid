package com.futechsoft.framework.security.auth;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
@Component
public class JwtTokenProvider {

    /*
	    private String secretKeyString="DSADsads";

	    private SecretKey secretKey;
	    private final long validityInMilliseconds = 3600000; // 1시간

	    @PostConstruct
	    protected void init() {
	        // 256비트 키 자동 생성
	        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	    }
	    */

	  //  private String secretKeyString = "your-secret-key-which-should-be-at-least-256-bits-long"; // 256 비트 이상의 키 문자열
	    private SecretKey secretKey;

	    @Value("${jwt.secretKey}")
	    private String secretKeyString;

	    //private final long validityInMilliseconds = 3600000; // 1시간
	    private final long validityInMilliseconds = 1800000; // 30분

	    @PostConstruct
	    protected void init() {
	    	/*
	        // secretKeyString을 바이트 배열로 변환
	        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);

	        // 키 길이가 256비트 이상이어야 한다.
	        if (keyBytes.length < 32) {
	            throw new IllegalArgumentException("Secret key must be at least 256 bits (32 bytes).");
	        }

	        // SecretKey 객체 생성
	        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
	        */

	        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
	        this.secretKey  = Keys.hmacShaKeyFor(keyBytes);
	    }

	    public static void main(String[] args) {
	        byte[] keyBytes = new byte[32]; // 32바이트 = 256비트
	        new SecureRandom().nextBytes(keyBytes);
	        String secretKey = Base64.getEncoder().encodeToString(keyBytes);
	        System.out.println("Your JWT Secret Key: " + secretKey);
	    }

	    /**
	     * JWT 토큰 생성
	     */
	    public String createToken(String username, Collection<? extends GrantedAuthority> authorities) {
	        Claims claims = Jwts.claims().setSubject(username);
	        List<String> roles = authorities.stream()
	                .map(GrantedAuthority::getAuthority)
	                .collect(Collectors.toList());
	        claims.put("roles", roles);

	        Date now = new Date();
	        Date validity = new Date(now.getTime() + validityInMilliseconds);

	        return Jwts.builder()
	                .setClaims(claims)
	                .setIssuedAt(now)
	                .setExpiration(validity)
	                .signWith(secretKey, SignatureAlgorithm.HS256)
	                .compact();
	    }


	    public boolean willExpireSoon(String token) {
	        try {
	            Claims claims = Jwts.parserBuilder()
	                    .setSigningKey(Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8)))
	                    .build()
	                    .parseClaimsJws(token)
	                    .getBody();

	            Date expiration = claims.getExpiration();
	            Date now = new Date();

	            long remainingTimeInSeconds = (expiration.getTime() - now.getTime()) / 1000;

	            System.out.println("remainingTimeInSeconds......."+remainingTimeInSeconds);


	            return remainingTimeInSeconds < 600;

	        } catch (Exception e) {
	            // 유효하지 않은 토큰은 재발급 대상으로 간주
	            return true;
	        }
	    }



	    /**
	     * 토큰 검증 및 파싱
	     */
	    public Jws<Claims> validateToken(String token) throws JwtException {
	        return Jwts.parserBuilder()
	                .setSigningKey(secretKey)
	                .build()
	                .parseClaimsJws(token);
	    }

	    /**
	     * 토큰에서 사용자 이름 추출
	     */
	    public String getUsername(String token) {
	        try {
	            return validateToken(token).getBody().getSubject();
	        } catch (JwtException e) {
	            return null; // 유효하지 않은 토큰이면 null 반환
	        }
	    }

	    /**
	     * 토큰에서 권한 정보 추출
	     */
	    public List<GrantedAuthority> getRoles(String token) {
	        try {
	            List<String> roles = (List<String>) validateToken(token).getBody().get("roles");
	            return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	        } catch (JwtException e) {
	        	  return Collections.emptyList(); // 유효하지 않은 토큰이면 빈 리스트 반환
	        }
	    }

	    /**
	     * 토큰이 유효한지 확인
	     */
	    public boolean isValidToken(String token, UserDetails userDetails) {
	        try {
	            String username = getUsername(token);
	            return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	        } catch (JwtException e) {
	            return false;
	        }
	    }


	    /**
	     * 토큰이 유효한지 확인
	     */
	    public boolean isValidTokenApiUser(String token) {
	        try {
	            return  !isTokenExpired(token);
	        } catch (JwtException e) {
	            return false;
	        }
	    }


	    /**
	     * 토큰의 유효기간 확인
	     */
	    public boolean isTokenExpired(String token) {
	        try {
	            return validateToken(token).getBody().getExpiration().before(new Date());
	        } catch (ExpiredJwtException e) {
	            return true;
	        }
	    }
}