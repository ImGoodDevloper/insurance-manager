package com.example.demo.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.security.service.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <pre>
JWT 身分驗證過濾器
 * <pre>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	@Autowired
	private JwtService jwtService;

	private static final String BEARER_PREFIX = "Bearer ";


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authheader = request.getHeader("Authorization");

		if (authheader != null && authheader.startsWith(BEARER_PREFIX)) {
			String token = authheader.substring(BEARER_PREFIX.length());
			Claims claim = jwtService.parseToken(token);
			String userName = claim.get("username", String.class);

			List<Map<String, String>> authoritiesMapList = claim.get("authorities", List.class);
			List<SimpleGrantedAuthority> authorities = new ArrayList<>();
			if (authoritiesMapList != null) {
				for (Map<String, String> map : authoritiesMapList) {
					authorities.add(new SimpleGrantedAuthority(map.get("authority")));
				}
			}
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName, null,
					authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}
}
