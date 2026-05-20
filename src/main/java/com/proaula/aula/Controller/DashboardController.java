package com.proaula.aula.Controller;

import com.proaula.aula.document.Usuario;
import com.proaula.aula.Service.BusService;
import com.proaula.aula.Service.BusUnificadoService;
import com.proaula.aula.Service.ContactoMensajeService;
import com.proaula.aula.Service.RutaService;
import com.proaula.aula.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RutaService rutaService;
    
    @Autowired
    private BusService busService;
    
    @Autowired
    private ContactoMensajeService contactoMensajeService;
    
    @Autowired
    private BusUnificadoService busUnificadoService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Spring Security ya protege esta ruta, solo verificar que el usuario exista en BD
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username);
        
        if (usuario == null) {
            return "redirect:/inicio-de-sesion-mejorado?error=usuario_no_encontrado";
        }
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("totalRutas", rutaService.count());
        model.addAttribute("rutasPopulares", rutaService.findTop4ByOrderByNombre());
        model.addAttribute("viajesRealizados", 0); // Se puede implementar después
        model.addAttribute("proximoViaje", "--");
        model.addAttribute("busesActivos", busService.countActiveBuses());
        
        return "dashboard-usuario";
    }
    
    /**
     * Dashboard de Administrador - Panel Principal
     */
    @GetMapping("/index_2")
    public String adminDashboard(Model model, Authentication authentication) {
        // Spring Security ya protege esta ruta con hasRole("ADMIN"), solo verificar usuario en BD
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username);
        
        if (usuario == null) {
            return "redirect:/admin-login?error=usuario_no_encontrado";
        }
        
        // Cargar estadísticas en tiempo real
        long totalUsuarios = usuarioService.count();
        long totalRutas = rutaService.count();
        long totalMensajes = contactoMensajeService.count();
        
        // Obtener estadísticas de buses de ambas bases de datos
        java.util.Map<String, Long> estadisticasBuses = busUnificadoService.obtenerEstadisticasBuses();
        long totalBuses = estadisticasBuses.get("total");
        
        // Agregar datos al modelo
        model.addAttribute("usuario", usuario);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalBuses", totalBuses);
        model.addAttribute("busesMySQL", estadisticasBuses.get("totalMySQL"));
        model.addAttribute("busesMongoDB", estadisticasBuses.get("totalMongoDB"));
        model.addAttribute("totalRutas", totalRutas);
        model.addAttribute("totalMensajes", totalMensajes);
        
        // Datos adicionales para actividad reciente (buses combinados)
        model.addAttribute("usuariosRecientes", usuarioService.getAllUsuarios());
        model.addAttribute("rutasRecientes", rutaService.getAllRutas());
        model.addAttribute("busesRecientes", busUnificadoService.obtenerTodosBuses());
        model.addAttribute("mensajesRecientes", contactoMensajeService.getAllMensajes());
        
        return "Admin/index2";
    }
}
