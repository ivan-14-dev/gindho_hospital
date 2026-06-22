package com.gindho.security;
import jakarta.servlet.FilterChain; import jakarta.servlet.ServletException; import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse; import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component; import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException; import java.util.stream.Collectors;
@Component @RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String h = req.getHeader("Authorization");
        if (h == null || !h.startsWith("Bearer ")) { chain.doFilter(req, res); return; }
        String token = h.substring(7);
        if (!jwtTokenProvider.validateToken(token)) { chain.doFilter(req, res); return; }
        String email = jwtTokenProvider.extractEmail(token);
        var auth = new UsernamePasswordAuthenticationToken(email, null,
                jwtTokenProvider.extractRoles(token).stream().map(r -> new SimpleGrantedAuthority(r.startsWith("ROLE_")?r:"ROLE_"+r)).collect(Collectors.toList()));
        auth.setDetails(token); SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(req, res);
    }
}