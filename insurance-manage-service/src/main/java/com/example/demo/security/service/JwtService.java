package com.example.demo.security.service;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
<pre>
JWT 認證服務
<pre/>
 */
public class JwtService {
	private final SecretKey secretKey;
	private final int validSeconds;
	private final JwtParser jwtParser;

	public JwtService(String secretKeyStr, int validSeconds) {
		this.secretKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
		this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
		this.validSeconds = validSeconds;
	}

	/**
	 * 產生access token
	 * @param user
	 * @return
	 */
	public String createLoginAccessToken(UserDetails user) {
		long expirationMillis = Instant.now().plusSeconds(validSeconds).getEpochSecond() * 1000;

		Claims claims = Jwts.claims()
				.issuedAt(new Date())
				.expiration(new Date(expirationMillis))
				.add("username", user.getUsername())
				.add("authorities", user.getAuthorities())
				.build();

		return Jwts.builder().claims(claims).signWith(secretKey).compact();
	}

	/**
	 * 產生refresh token
	 * @param user
	 * @return
	 */
	public String createLoginRefreshToken(UserDetails user) {
		//TODO
		return null;
	}

	/**
	 * 驗證token
	 * @param jwt
	 * @return
	 * @throws JwtException
	 */
	public Claims parseToken(String jwt) throws JwtException {
		return jwtParser.parseSignedClaims(jwt).getPayload();
	}
}
