package com.proaula.aula.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proaula.aula.document.Usuario;
import com.proaula.aula.Service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public Usuario register(@RequestBody Usuario usuario) {
        return usuarioService.register(usuario);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        Usuario user = usuarioService.login(credentials.get("username"), credentials.get("password"));
        if (user != null) {
            return Map.of("user", user, "menu", user.getRole().equals("ADMIN") ? "Admin Menu: Full CRUD" : "User Menu: View Info");
        }
        return Map.of("error", "Invalid credentials");
    }
}