package com.beyond.meongnyang.common.security;

import com.beyond.meongnyang.common.handler.JwtAuthenticationHandler;
import com.beyond.meongnyang.common.handler.JwtAuthorizationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity

public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final JwtAuthorizationHandler jwtAuthorizationHandler;
    private final JwtAuthenticationHandler jwtAuthenticationHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .cors(c -> c.configurationSource(corsConfiguration()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(jwtAuthenticationHandler) // 401error
                        .accessDeniedHandler(jwtAuthorizationHandler))  // 403error
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight
                        .requestMatchers(
                                "/users/sign", "/users/login/**", "/users/find/email", "/users/check-email", "/users/check-nickname", "/users/check-phone", "/connect/**",
                                "/users/verify-email", "/users/verify-email-check", "/users/lost-password",
                                "/users/signup-extra",
                                "/users/token/refresh",
                                "/users/logout",
                                "/users/link/confirm",
                                "/health")
                        .permitAll().anyRequest().authenticated())
                .build();
    }

@Bean
public CorsConfigurationSource corsConfiguration() {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowCredentials(true);
    c.setAllowedOriginPatterns(List.of("http://localhost:3000","http://localhost:5173","https://*.mydomain.com"));
    c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With","X-Refresh-Token"));
    c.setExposedHeaders(List.of("X-Refresh-Token"));
    c.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", c);
    return src;
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
