package com.example.demo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.security.filter.JwtAuthenticationFilter;
import com.example.demo.security.service.JwtService;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * <pre>
 * Spring Security 全域安全性配置
 * </pre>
 */
@Configuration
@EnableWebSecurity(debug = true)
@SecurityScheme(
	    name = "Bearer Authentication",
	    type = SecuritySchemeType.HTTP,
	    bearerFormat = "JWT",
	    scheme = "bearer",
	    description = "請輸入從 /login 取得的 JWT Token"
	)
public class SecurityConfig {

	/**
	 * 配置SecurityFilterChain
	 * 
	 * @param httpSecurity
	 * @param jwtAuthFilter
	 * @return
	 * @throws Exception
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
		return httpSecurity.csrf(csrfConf -> csrfConf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(request -> request.requestMatchers("/login").permitAll()
						// 放行 Swagger UI 與 OpenAPI 相關路徑
		                .requestMatchers("/v3/api-docs/**").permitAll()
		                .requestMatchers("/swagger-ui/**").permitAll()
		                .requestMatchers("/swagger-ui.html").permitAll()
		                .requestMatchers("/webjars/**").permitAll()
				.anyRequest().authenticated())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	/**
	 * 配置測試使用者帳號
	 * 
	 * @param encoder
	 * @return
	 */
	@Bean
	public UserDetailsService userDetailsManager(PasswordEncoder encoder) {
		UserDetails user = User.withUsername("tester")
				.password(encoder.encode("test123"))
				.authorities("ADMIN")
				.build();
		return new InMemoryUserDetailsManager(List.of(user));
	}

	/**
	 * 配置密碼雜湊加密算法 (BCrypt)
	 * 
	 * @return
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * 設定 JWT 服務
	 * 
	 * @param secretKeyStr
	 * @param validSeconds
	 * @return
	 */
	@Bean
	public JwtService jwtService(@Value("${jwt.secret-key}") String secretKeyStr,
			@Value("${jwt.valid-seconds}") int validSeconds) {
		return new JwtService(secretKeyStr, validSeconds);
	}
}
