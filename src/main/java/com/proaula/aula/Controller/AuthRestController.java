package com.proaula.aula.Controller;

import com.proaula.aula.config.JwtCookieService;
import com.proaula.aula.config.JwtTokenProvider;
import com.proaula.aula.document.Usuario;
import com.proaula.aula.dto.LoginRequest;
import com.proaula.aula.Service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private JwtCookieService jwtCookieService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public Map<String, String> loginInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Envía una petición POST a este endpoint con username y password.");
        response.put("usage", "POST /api/auth/login?username=...&password=...");
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam String username, @RequestParam String password, HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = tokenProvider.generateToken(authentication);
            jwtCookieService.addJwtCookie(request, response, token);

            return ResponseEntity.ok().build();
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<Void> loginJson(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String token = tokenProvider.generateToken(authentication);
            jwtCookieService.addJwtCookie(httpRequest, response, token);

            return ResponseEntity.ok().build();
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Usuario usuario) {
        if (usuarioService.existsByUsername(usuario.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "El nombre de usuario ya existe"));
        }
        if (usuarioService.existsByEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "El email ya está registrado"));
        }
        usuarioService.register(usuario);
        return ResponseEntity.ok(Map.of("message", "Usuario registrado correctamente"));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }

    @GetMapping("/test")
    @PreAuthorize("isAuthenticated()")
    public Map<String, String> testToken(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "¡Token JWT válido!");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities().toString());
        return response;
    }
}