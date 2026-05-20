package com.proaula.aula.Controller;

import com.proaula.aula.Service.UsuarioService;
import com.proaula.aula.document.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OAuth2Controller {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/oauth2/complete-password")
    public String completePasswordPage(Model model, Authentication authentication, HttpSession session) {
        String username = null;
        if (authentication != null && authentication.getName() != null) {
            username = authentication.getName();
        }
        if (username == null && session != null) {
            username = (String) session.getAttribute("OAUTH2_USERNAME");
        }
        if (username == null) {
            return "redirect:/inicio-de-sesion-mejorado";
        }

        model.addAttribute("username", username);
        session.setAttribute("OAUTH2_USERNAME", username);
        return "oauth2-complete-password";
    }

    @PostMapping("/oauth2/complete-password")
    public String completePasswordSubmit(@RequestParam String password,
                                         @RequestParam String confirmPassword,
                                         Authentication authentication,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        String username = null;
        if (authentication != null && authentication.getName() != null) {
            username = authentication.getName();
        }
        if (username == null && session != null) {
            username = (String) session.getAttribute("OAUTH2_USERNAME");
        }
        if (username == null) {
            redirectAttributes.addFlashAttribute("error", "Sesión inválida. Por favor inicia sesión nuevamente.");
            return "redirect:/inicio-de-sesion-mejorado";
        }

        if (password == null || password.trim().isEmpty() || confirmPassword == null || confirmPassword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Ambas contraseñas son obligatorias.");
            return "redirect:/oauth2/complete-password";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            return "redirect:/oauth2/complete-password";
        }

        if (password.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            return "redirect:/oauth2/complete-password";
        }

        Usuario usuario = usuarioService.findByUsername(username);
        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado. Vuelve a intentarlo.");
            return "redirect:/inicio-de-sesion-mejorado";
        }

        if (usuario.isPasswordSetupRequired()) {
            usuarioService.completePasswordSetup(username, password);
            if (session != null) {
                session.removeAttribute("OAUTH2_USERNAME");
            }
            redirectAttributes.addFlashAttribute("success", "Contraseña establecida. Ya puedes continuar.");
            return "redirect:/dashboard";
        }

        try {
            usuarioService.login(username, password);
            if (session != null) {
                session.removeAttribute("OAUTH2_USERNAME");
            }
            return "redirect:/dashboard";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Contraseña incorrecta. Intenta nuevamente.");
            return "redirect:/oauth2/complete-password";
        }
    }
}
