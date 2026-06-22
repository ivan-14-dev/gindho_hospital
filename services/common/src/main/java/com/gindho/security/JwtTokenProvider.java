package com.gindho.security;
import io.jsonwebtoken.*; import io.jsonwebtoken.io.Decoders; import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j; import org.springframework.beans.factory.annotation.Value; import org.springframework.stereotype.Component;
import javax.crypto.SecretKey; import java.util.Date; import java.util.List; import java.util.function.Function;
@Slf4j @Component
public class JwtTokenProvider {
    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}") private String secret;
    public String extractEmail(String token) { return extractClaim(token, Claims::getSubject); }
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) { return extractAllClaims(token).get("roles", List.class); }
    public <T> T extractClaim(String token, Function<Claims, T> f) { return f.apply(extractAllClaims(token)); }
    public boolean validateToken(String token) {
        try { extractAllClaims(token); return !extractExpiration(token).before(new Date()); }
        catch (JwtException | IllegalArgumentException e) { log.warn("Invalid JWT: {}", e.getMessage()); return false; }
    }
    private Date extractExpiration(String token) { return extractClaim(token, Claims::getExpiration); }
    private Claims extractAllClaims(String token) { return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))).build().parseSignedClaims(token).getPayload(); }
}