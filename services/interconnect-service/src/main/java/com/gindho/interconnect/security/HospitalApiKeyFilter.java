package com.gindho.interconnect.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gindho.interconnect.model.HospitalPartner;
import com.gindho.interconnect.repository.HospitalPartnerRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HospitalApiKeyFilter extends OncePerRequestFilter {

    private final HospitalPartnerRepository hospitalPartnerRepository;

    private static final String API_KEY_HEADER = "X-Hospital-API-Key";
    private static final String HOSPITAL_ID_HEADER = "X-Hospital-ID";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api/interconnect/public/") || path.startsWith("/actuator/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        String hospitalId = request.getHeader(HOSPITAL_ID_HEADER);

        if (apiKey == null || hospitalId == null) {
            log.warn("Missing API key or hospital ID from {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Missing authentication headers: X-Hospital-API-Key and X-Hospital-ID required\"}");
            return;
        }

        HospitalPartner partner = hospitalPartnerRepository.findByHospitalId(hospitalId).orElse(null);
        if (partner == null || !partner.getApiKey().equals(apiKey)) {
            log.warn("Invalid API key or hospital ID: {} from {}", hospitalId, request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid credentials\"}");
            return;
        }

        if (partner.getStatus() != HospitalPartner.PartnerStatus.ACTIVE) {
            log.warn("Hospital {} is not active (status: {})", hospitalId, partner.getStatus());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"Hospital account is not active\"}");
            return;
        }

        var auth = new UsernamePasswordAuthenticationToken(
                partner, null, java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}