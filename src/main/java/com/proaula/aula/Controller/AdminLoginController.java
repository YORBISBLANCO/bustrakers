package com.proaula.aula.Controller;

import com.proaula.aula.Service.AdminCodeService;
import com.proaula.aula.Service.UsuarioService;
import com.proaula.aula.document.AdminCode;
import com.proaula.aula.Repository.mongodb.AdminCodeRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import com.proaula.aula.config.JwtCookieService;
import com.proaula.aula.config.JwtTokenProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;

@Controller
public class AdminLoginController {
    
    private static final Logger log = LoggerFactory.getLogger(AdminLoginController.class);
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private AdminCodeService adminCodeService;
    
    @Autowired
    private AdminCodeRepository adminCodeRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private JwtCookieService jwtCookieService;
    
    /**
     * Mostrar formulario de login administrativo
     */
    @GetMapping("/admin-login")
    public String mostrarLoginAdmin(
            @RequestParam(required = false) Boolean verified,
            HttpSession session,
            Model model) {
        
        // Siempre mostrar como verificado para acceso directo
        model.addAttribute("verified", true);
        
        return "admin-login";
    }
    
    /**
     * Endpoint de debug para verificar códigos de admin
     */
    @GetMapping("/debug/admin-codes")
    @ResponseBody
    public String debugAdminCodes() {
        List<AdminCode> allCodes = adminCodeRepository.findAll();
        StringBuilder result = new StringBuilder("Códigos de administrador en BD:\n");
        for (AdminCode code : allCodes) {
            result.append("- ID: ").append(code.getId())
                  .append(", Código: '").append(code.getCodigo()).append("'\n");
        }
        result.append("Total: ").append(allCodes.size()).append(" códigos\n");
        
        // Probar validación
        boolean isValid2026 = adminCodeService.isValidAdminCode("ADMIN2026");
        result.append("¿ADMIN2026 es válido? ").append(isValid2026).append("\n");
        
        return result.toString();
    }
    
    /**
     * Paso 1: Verificar código de administrador
     */
    @PostMapping("/admin/verificar-codigo")
    public String verificarCodigoAdmin(
            @RequestParam(value = "codigoAdmin", required = false) String codigoAdmin,
            @RequestParam(value = "codigo", required = false) String codigo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        String codigoIngresado = (codigoAdmin != null && !codigoAdmin.trim().isEmpty()) ? codigoAdmin.trim() : (codigo != null ? codigo.trim() : null);
        log.info("Verificando código de admin: {}", codigoIngresado);
        
        if (adminCodeService.isValidAdminCode(codigoIngresado)) {
            session.setAttribute("adminCodeVerified", true);
            log.info("✅ Código de admin válido: {}", codigo);
            redirectAttributes.addFlashAttribute("success", "✅ Código verificado correctamente. Ahora ingrese sus credenciales de administrador.");
            return "redirect:/admin-login?verified=true";
        } else {
            log.warn("❌ Código de admin inválido: {}", codigo);
            redirectAttributes.addFlashAttribute("error", "❌ Código de administrador incorrecto");
            return "redirect:/admin-login";
        }
    }
    
    /**
     * Paso 2: Autenticar con credenciales de usuario ADMIN
     */
    @PostMapping("/admin/login")
    public String loginAdmin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        
        // Verificación del código de admin removida para acceso directo
        
        try {
            // Validar primero en la BD que el usuario sea ADMIN
            if (!usuarioService.isAdminUser(username)) {
                log.warn("Intento de login de admin con usuario no-admin: {}", username);
                redirectAttributes.addFlashAttribute("error", "❌ Este usuario no tiene permisos de administrador");
                return "redirect:/admin-login?verified=true";
            }
            
            // Autenticar con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // Doble verificación: validar que el usuario tenga rol ADMIN en authorities
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            boolean isAdmin = authorities.stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
            
            if (!isAdmin) {
                // Usuario no es administrador
                log.error("Usuario {} autenticado pero sin rol ADMIN. Roles obtenidos: {}", username, authorities);
                redirectAttributes.addFlashAttribute("error", "❌ Error de roles: Este usuario no tiene permisos de administrador");
                return "redirect:/admin-login?verified=true";
            }
            
            // ✅ IMPORTANTE: Establecer la autenticación solo para esta petición
            // y generar un JWT para las siguientes peticiones.
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            
            String jwtToken = tokenProvider.generateToken(authentication);
            jwtCookieService.addJwtCookie(request, response, jwtToken);
            
            // Guardar información adicional en sesión solo como dato auxiliar
            session.setAttribute("usuarioAutenticado", username);
            session.setAttribute("rolUsuario", "ADMIN");
            session.removeAttribute("adminCodeVerified"); // Limpiar código verificado
            
            log.info("✅ Admin login successful for user: {}, redirecting to /index_2", username);
            log.info("✅ JWT cookie issued for admin login. Roles: {}", authorities);
            
            // Redirigir al dashboard de administrador
            return "redirect:/index_2";
            
        } catch (BadCredentialsException e) {
            log.warn("Intento de login fallido para usuario: {} - Credenciales inválidas", username);
            redirectAttributes.addFlashAttribute("error", "❌ Usuario o contraseña incorrectos");
            return "redirect:/admin-login?verified=true";
        } catch (Exception e) {
            log.error("Error al iniciar sesión como admin para usuario: {}", username, e);
            redirectAttributes.addFlashAttribute("error", "❌ Error al iniciar sesión: " + e.getMessage());
            return "redirect:/admin-login?verified=true";
        }
    }
    
    /**
     * Logout de administrador
     */
    @GetMapping("/admin/logout")
    public String logoutAdmin(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            SecurityContextHolder.clearContext();
            session.removeAttribute("adminCodeVerified");
            session.invalidate();
            redirectAttributes.addFlashAttribute("success", "✅ Sesión cerrada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cerrar sesión");
        }
        return "redirect:/admin-login";
    }
}
