package com.proaula.aula.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.proaula.aula.document.Usuario;
import com.proaula.aula.document.Viaje;
import com.proaula.aula.Repository.mongodb.UsuarioRepository;
import com.proaula.aula.exception.AulaException;
import com.proaula.aula.exception.UsuarioNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService() {
    }

    public Usuario register(Usuario usuario) {
        // Normalizar role para garantizar compatibilidad con Spring Security
        String role = usuario.getRole();
        if (role == null || role.trim().isEmpty()) {
            usuario.setRole("ROLE_USER");
        } else {
            role = role.trim().toUpperCase();
            // Normalizar para asegurar que tenga el prefijo ROLE_
            if (!role.startsWith("ROLE_")) {
                usuario.setRole("ROLE_" + role);
            } else {
                usuario.setRole(role);
            }
        }

        // Encriptar la contraseña antes de guardar
        String encodedPassword = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(encodedPassword);
        return usuarioRepository.save(usuario);
    }
    
    /**
     * Verifica si un usuario existe y tiene rol ADMIN
     */
    public boolean isAdminUser(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        return usuario.isPresent() && 
               usuario.get().getRole() != null && 
               usuario.get().getRole().equalsIgnoreCase("ROLE_ADMIN");
    }
    
    /**
     * Obtiene un usuario por username y valida su rol ADMIN
     */
    public Usuario getAdminByUsername(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isPresent() && 
            usuario.get().getRole() != null && 
            usuario.get().getRole().equalsIgnoreCase("ROLE_ADMIN")) {
            return usuario.get();
        }
        return null;
    }

    public Usuario     // Guardar cookie manualmente
  

    login(String username, String password) {
        Optional<Usuario> user = usuarioRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new UsuarioNotFoundException(username);
        }
        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new AulaException("Contraseña incorrecta", "INVALID_PASSWORD");
        }
        return user.get();
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioById(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void deleteById(String id) {
        if (id != null) {
            usuarioRepository.deleteById(id);
        }
    }

    public Usuario findByUsername(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        return usuario.orElse(null);
    }

    public Usuario agregarViajeAUsuario(String username, Viaje viaje) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        if (viaje == null) {
            throw new IllegalArgumentException("El viaje no puede ser nulo");
        }
        if (usuario.getViajes() == null) {
            usuario.setViajes(new ArrayList<>());
        }
        usuario.getViajes().add(viaje);
        return usuarioRepository.save(usuario);
    }

    public Usuario findByEmail(String email) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        return usuario.orElse(null);
    }

    public long count() {
        return usuarioRepository.count();
    }

    public Usuario updateUsuario(String id, Usuario usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        
        usuario.setNombres(usuarioDetails.getNombres());
        usuario.setApellidos(usuarioDetails.getApellidos());
        usuario.setEmail(usuarioDetails.getEmail());
        
        return usuarioRepository.save(usuario);
    }

    public Usuario changePassword(String id, String newPassword) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        usuario.setPassword(encodedPassword);
        
        return usuarioRepository.save(usuario);
    }

    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean isPasswordSetupRequired(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        return usuario.map(Usuario::isPasswordSetupRequired).orElse(false);
    }

    public Usuario completePasswordSetup(String username, String newPassword) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        String encodedPassword = passwordEncoder.encode(newPassword);
        usuario.setPassword(encodedPassword);
        usuario.setPasswordSetupRequired(false);
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findByIdOptional(String id) {
        return usuarioRepository.findById(id);
    }
}