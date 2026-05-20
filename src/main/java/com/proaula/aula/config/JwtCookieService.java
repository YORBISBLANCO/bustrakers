package com.proaula.aula.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtCookieService {

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    public void addJwtCookie(HttpServletRequest request, HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(JwtAuthenticationFilter.JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpirationMs / 1000)); // Duración igual al token JWT
        response.addCookie(cookie);
    }
    
    public void clearJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(JwtAuthenticationFilter.JWT_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
