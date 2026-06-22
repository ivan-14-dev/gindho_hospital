package com.gindho.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

/**
 * Service pour gérer les tokens JWT via cookies HttpOnly.
 * Alternative plus sécurisée au stockage localStorage côté client.
 */
@Service
public class TokenCookieService {

    private static final String TOKEN_COOKIE = "gindho_token";
    private static final String REFRESH_COOKIE = "gindho_refresh";

    public void setTokenCookies(HttpServletResponse response, String token, String refreshToken, long maxAge) {
        addCookie(response, TOKEN_COOKIE, token, (int) maxAge, true);
        addCookie(response, REFRESH_COOKIE, refreshToken, (int) (maxAge * 2), true);
    }

    public void clearTokenCookies(HttpServletResponse response) {
        addCookie(response, TOKEN_COOKIE, "", 0, true);
        addCookie(response, REFRESH_COOKIE, "", 0, true);
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(httpOnly);       // Pas accessible via JavaScript
        cookie.setSecure(true);             // HTTPS uniquement
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setAttribute("SameSite", "Strict"); // Anti-CSRF
        response.addCookie(cookie);
    }
}
