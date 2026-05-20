package com.proaula.aula.config;

import com.proaula.aula.config.JwtCookieService;
import com.proaula.aula.config.JwtTokenProvider;
import com.proaula.aula.Service.EmailService;
import com.proaula.aula.Service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtCookieService jwtCookieService;
    private final UsuarioService usuarioService;

    @org.springframework.beans.factory.annotation.Autowired
    private EmailService emailService;

    public CustomAuthenticationSuccessHandler(JwtTokenProvider tokenProvider,
                                              JwtCookieService jwtCookieService,
                                              UsuarioService usuarioService) {
        this.tokenProvider = tokenProvider;
        this.jwtCookieService = jwtCookieService;
        this.usuarioService = usuarioService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {

        String token = tokenProvider.generateToken(authentication);
        jwtCookieService.addJwtCookie(request, response, token);

        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        // Obtener email del principal (OAuth2User o usuario local)
        String email = null;
        String principalName = authentication.getName();
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) principal;
            Object attrEmail = oauth2User.getAttributes().get("email");
            if (attrEmail != null) email = attrEmail.toString();
        }
        if (email == null) {
            var usuario = usuarioService.findByUsername(principalName);
            if (usuario != null) email = usuario.getEmail();
        }

        // Enviar notificación de login aunque el usuario de OAuth2 necesite crear contraseña
        try {
            if (email != null && !email.isBlank()) {
                emailService.sendLoginNotification(email, principalName);
            }
        } catch (Exception e) {
            System.err.println("No se pudo enviar notificación de login: " + e.getMessage());
        }

        if (usuarioService.isPasswordSetupRequired(authentication.getName())) {
            HttpSession session = request.getSession(true);
            session.setAttribute("OAUTH2_USERNAME", authentication.getName());
            response.sendRedirect("/oauth2/complete-password");
            return;
        }

        if (isAdmin) {
            response.sendRedirect("/index_2");
        } else {
            response.sendRedirect("/dashboard");
        }
    }
}