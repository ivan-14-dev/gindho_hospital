package com.gindho.authorization.config;

import com.gindho.authorization.security.KeycloakJwtAuthConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KeycloakJwtAuthConverter keycloakJwtAuthConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/authorize").authenticated()
                .requestMatchers("/api/authorization/check").authenticated()
                .requestMatchers("/api/authorization/permissions").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/authorization/roles").hasRole("ADMIN")
                .requestMatchers("/api/authorization/roles").hasRole("ADMIN")
                .requestMatchers("/api/authorization/roles/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/authorization/permissions").hasRole("ADMIN")
                .requestMatchers("/api/authorization/permissions/all").hasRole("ADMIN")
                .requestMatchers("/api/authorization/permissions/catalog").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/authorization/assign").hasRole("ADMIN")
                .requestMatchers("/api/authorization/users/**").hasRole("ADMIN")
                .requestMatchers("/api/authorization/role-templates").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().denyAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthConverter))
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
            );
        return http.build();
    }
}