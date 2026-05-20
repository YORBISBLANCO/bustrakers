package com.proaula.aula.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Optional;

import com.proaula.aula.document.ContactoMensaje;
import com.proaula.aula.document.Usuario;
import com.proaula.aula.document.Viaje;
import com.proaula.aula.Repository.mongodb.UsuarioRepository;
import com.proaula.aula.Service.AdminCodeService;
import com.proaula.aula.Service.BusService;
import com.proaula.aula.Service.ContactoMensajeService;
import com.proaula.aula.Service.RutaService;
import com.proaula.aula.Service.UsuarioService;

@Controller
public class HomeController {
    @Autowired
    private BusService busService;
    @Autowired
    private RutaService rutaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AdminCodeService adminCodeService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ContactoMensajeService contactoMensajeService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping({"/inicio-de-sesion-mejorado.html", "/inicio-de-sesion-mejorado", "/inicio_de_sesion"})
    public String login(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "inicio-de-sesion-mejorado";
    }

    @GetMapping({"/registro-mejorado.html", "/registro-mejorado", "/registro"})
    public String registro(Model model) {
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        return "registro-mejorado";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario, 
                           @RequestParam(required = false) String adminCode,
                           @RequestParam(required = false) String passwordConfirm,
                           @RequestParam(required = false) boolean terminos,
                           Model model) {
        // Verificar términos y condiciones
        if (!terminos) {
            model.addAttribute("error", "Debes aceptar los términos y condiciones para registrarte");
            model.addAttribute("usuario", usuario);
            return "registro-mejorado";
        }
        
        // Verificar que las contraseñas coincidan
        if (passwordConfirm == null || !passwordConfirm.equals(usuario.getPassword())) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            model.addAttribute("usuario", usuario);
            return "registro-mejorado";
        }
        
        // Verificar si el nombre de usuario ya existe
        Usuario existingUser = usuarioService.findByUsername(usuario.getUsername());
        if (existingUser != null) {
            model.addAttribute("error", "El nombre de usuario ya está en uso");
            model.addAttribute("usuario", usuario); // Mantener datos del formulario
            return "registro-mejorado";
        }
        
        // Verificar si el email ya está en uso
        Optional<Usuario> existingByEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingByEmail.isPresent()) {
            model.addAttribute("error", "El email ya está registrado");
            model.addAttribute("usuario", usuario); // Mantener datos del formulario
            return "registro-mejorado";
        }
        
        // Normalizar y verificar código de administrador si selecciona rol ADMIN
        String roleIngresado = usuario.getRole() != null ? usuario.getRole().trim().toUpperCase() : "";
        
        if ("ADMIN".equals(roleIngresado)) {
            // Código de admin removido para registro directo como admin
            usuario.setRole("ROLE_ADMIN");
        } else {
            // Si no es ADMIN, asignar USER por defecto
            usuario.setRole("ROLE_USER");
        }
        
        usuarioService.register(usuario);
        
        // Redirigir según el rol del usuario
        if ("ROLE_ADMIN".equals(usuario.getRole())) {
            return "redirect:/inicio-de-sesion-mejorado?registrado=true&admin=true";
        } else {
            return "redirect:/inicio-de-sesion-mejorado?registrado=true";
        }
    }

    // NOTA: El login ahora se maneja con Spring Security en SecurityConfig
    // Este método fue eliminado para evitar conflictos

    @GetMapping("/index_3")
    public String index3(Model model) {
        model.addAttribute("usuario", new Usuario()); // Simula usuario logueado
        return "Usuario/index_3";
    }

    @GetMapping({"/public_index_3", "/index_3_public"})
    public String publicIndex3(Model model) {
        // Página pública: no requiere usuario autenticado
        model.addAttribute("usuario", null);
        model.addAttribute("contactoMensaje", new ContactoMensaje());
        return "Usuario/index_3_public";
    }



    @GetMapping("/viajar")
    public String viajar(Model model, Authentication authentication) {
        Usuario usuario = obtenerUsuarioActual(authentication);
        model.addAttribute("usuario", usuario != null ? usuario : new Usuario());
        return "Usuario/viajar";
    }

    @GetMapping("/contacto_usuario")
    public String contactoUsuario(Model model) {
        model.addAttribute("contactoMensaje", new ContactoMensaje());
        model.addAttribute("usuario", new Usuario()); // Simulación - en realidad debería obtener el usuario autenticado
        return "Usuario/contacto";
    }

    @PostMapping("/contacto_usuario")
    public String contactoUsuarioSubmit(@ModelAttribute ContactoMensaje contactoMensaje, Model model) {
        // Guardar el mensaje de contacto en la base de datos
        contactoMensajeService.guardarMensaje(contactoMensaje);
        model.addAttribute("mensaje", "Gracias por tu mensaje. Nos pondremos en contacto contigo pronto.");
        model.addAttribute("contactoMensaje", new ContactoMensaje()); // Limpiar formulario
        model.addAttribute("usuario", new Usuario()); // Simulación - en realidad debería obtener el usuario autenticado
        return "Usuario/contacto";
    }

    @GetMapping("/viajar_public")
    public String viajarPublic(Model model) {
        // Página pública de viajes: sin guardar datos
        return "Usuario/viajar_public";
    }

    @PostMapping("/viajar")
    public String guardarViajeAutenticado(@ModelAttribute Viaje viaje, Authentication authentication, Model model) {
        Authentication currentAuth = authentication != null ? authentication : SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = obtenerUsuarioActual(currentAuth);

        if (usuario == null) {
            model.addAttribute("error", "Tu sesión expiró. Por favor inicia sesión de nuevo.");
            return "redirect:/inicio-de-sesion-mejorado";
        }

        viaje.setReservadoEn(System.currentTimeMillis());
        Usuario actualizado = usuarioService.agregarViajeAUsuario(usuario.getUsername(), viaje);

        model.addAttribute("mensaje", "Viaje reservado correctamente");
        model.addAttribute("usuario", actualizado);
        return "Usuario/viajar";
    }

    @GetMapping("/historial")
    public String historial(Model model, Authentication authentication) {
        Usuario usuario = obtenerUsuarioActual(authentication);
        if (usuario == null) {
            return "redirect:/inicio-de-sesion-mejorado";
        }
        model.addAttribute("usuario", usuario);
        model.addAttribute("viajes", usuario.getViajes());
        return "Usuario/historial";
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return usuarioService.findByUsername(authentication.getName());
    }

    @GetMapping("/contacto_public")
    public String contactoPublic(Model model) {
        model.addAttribute("contactoMensaje", new ContactoMensaje());
        return "Usuario/index_3_public";
    }

    @PostMapping("/contacto_public")
    public String contactoPublic(@ModelAttribute ContactoMensaje contactoMensaje, Model model) {
        // Guardar el mensaje de contacto en la base de datos
        contactoMensajeService.guardarMensaje(contactoMensaje);
        model.addAttribute("mensaje", "Gracias por tu consulta. Nos pondremos en contacto pronto.");
        model.addAttribute("contactoMensaje", new ContactoMensaje()); // Limpiar formulario
        return "Usuario/index_3_public";
    }

    @GetMapping("/gestionar-usuarios")
    public String gestionarUsuarios(@RequestParam(value = "buscar", defaultValue = "") String buscar, Model model) {
        // Cargar todos los usuarios desde la base de datos y separarlos por rol
        List<Usuario> todos = usuarioService.getAllUsuarios();

        // Filtrar por búsqueda si está presente
        if (buscar != null && !buscar.isEmpty()) {
            String buscarLower = buscar.toLowerCase();
            todos = todos.stream()
                    .filter(u -> (u.getNombres() != null && u.getNombres().toLowerCase().contains(buscarLower))
                            || (u.getApellidos() != null && u.getApellidos().toLowerCase().contains(buscarLower))
                            || (u.getEmail() != null && u.getEmail().toLowerCase().contains(buscarLower))
                            || (u.getUsername() != null && u.getUsername().toLowerCase().contains(buscarLower)))
                    .collect(Collectors.toList());
        }

        List<com.proaula.aula.document.Usuario> administradores = todos.stream()
                .filter(u -> u.getRole() != null && u.getRole().equalsIgnoreCase("ADMIN"))
                .sorted(Comparator.comparing(com.proaula.aula.document.Usuario::getNombres, Comparator.nullsLast(String::compareTo))
                        .thenComparing(com.proaula.aula.document.Usuario::getApellidos, Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());

        List<com.proaula.aula.document.Usuario> usuarios = todos.stream()
                .filter(u -> u.getRole() == null || !u.getRole().equalsIgnoreCase("ADMIN"))
                .sorted(Comparator.comparing(com.proaula.aula.document.Usuario::getNombres, Comparator.nullsLast(String::compareTo))
                        .thenComparing(com.proaula.aula.document.Usuario::getApellidos, Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());

        model.addAttribute("administradores", administradores);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("buscar", buscar);
        model.addAttribute("mensaje", "Gestión de usuarios");
        return "Admin/gestionar_usuarios";
    }

    @GetMapping("/editar-usuario/{id}")
    public String editarUsuario(@PathVariable String id, Model model) {
        com.proaula.aula.document.Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return "redirect:/gestionar-usuarios";
        }
        model.addAttribute("usuario", usuario);
        return "Admin/editar_usuario";
    }

    @GetMapping("/eliminar-usuario/{id}")
    public String eliminarUsuario(@PathVariable String id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        com.proaula.aula.document.Usuario u = usuarioService.getUsuarioById(id);
        if (u != null) {
            usuarioService.deleteById(id);
            redirectAttrs.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
        } else {
            redirectAttrs.addFlashAttribute("mensaje", "Usuario no encontrado.");
        }
        return "redirect:/gestionar-usuarios";
    }

    @PostMapping("/actualizar-usuario")
    public String actualizarUsuario(@ModelAttribute com.proaula.aula.document.Usuario usuario) {
        // Mantener contraseña actual si el administrador no ingresó nueva
        com.proaula.aula.document.Usuario existente = usuarioService.getUsuarioById(usuario.getId());
        if (existente != null) {
            if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
                usuario.setPassword(existente.getPassword());
            }
        }
        usuarioService.register(usuario);
        return "redirect:/gestionar-usuarios";
    }

    @PostMapping("/admin-crear-usuario")
    public String crearUsuario(@ModelAttribute Usuario usuario, Model model) {
        // Verificar si el username ya existe
        Usuario existingUser = usuarioService.findByUsername(usuario.getUsername());
        if (existingUser != null) {
            model.addAttribute("error", "El nombre de usuario ya está en uso");
            model.addAttribute("usuario", usuario);
            return "Admin/gestionar_usuarios";
        }
        
        // Verificar si el email ya existe
        Optional<Usuario> existingByEmail = usuarioRepository.findByEmail(usuario.getEmail());
        if (existingByEmail.isPresent()) {
            model.addAttribute("error", "El email ya está registrado");
            model.addAttribute("usuario", usuario);
            return "Admin/gestionar_usuarios";
        }
        
        // Asignar role ADMIN automáticamente
        usuario.setRole("ROLE_ADMIN");
        usuarioService.register(usuario);
        return "redirect:/gestionar-usuarios?creado=true";
    }

    @GetMapping("/reportes")
    public String reportes(@RequestParam(value = "buscar", defaultValue = "") String buscar, Model model) {
        List<com.proaula.aula.document.Bus> buses = busService.getAllBuses();
        
        // Filtrar por búsqueda si está presente
        if (buscar != null && !buscar.isEmpty()) {
            String buscarLower = buscar.toLowerCase();
            buses = buses.stream()
                    .filter(b -> (b.getPlaca() != null && b.getPlaca().toLowerCase().contains(buscarLower))
                            || (b.getModelo() != null && b.getModelo().toLowerCase().contains(buscarLower)))
                    .collect(Collectors.toList());
        }
        
        model.addAttribute("totalBuses", busService.getAllBuses().size());
        model.addAttribute("buses", buses);
        model.addAttribute("totalRutas", rutaService.getAllRutas().size());
        model.addAttribute("totalUsuarios", usuarioService.getAllUsuarios().size());
        model.addAttribute("usuariosActivos", usuarioService.getAllUsuarios().size());
        model.addAttribute("buscar", buscar);
        return "Admin/reportes";
    }

    @GetMapping("/consultas")
    public String consultas(Model model) {
        model.addAttribute("buses", busService.getAllBuses());
        return "Usuario/consultas";
    }

    @GetMapping("/mensajes_contacto")
    public String mensajesContacto(Model model) {
        List<ContactoMensaje> mensajes = contactoMensajeService.getAllMensajes();
        model.addAttribute("mensajes", mensajes);
        return "Admin/lista_mensajes";
    }

    @GetMapping("/eliminar_mensaje/{id}")
    public String eliminarMensaje(@PathVariable String id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttrs) {
        contactoMensajeService.deleteMensaje(id);
        redirectAttrs.addFlashAttribute("mensaje", "Mensaje eliminado correctamente.");
        return "redirect:/mensajes_contacto";
    }

    @GetMapping("/privacidad")
    public String privacidad() {
        return "privacidad";
    }

    @GetMapping("/privacidad.html")
    public String privacidadHtml() {
        return "privacidad";
    }

    @GetMapping("/terminos")
    public String terminos() {
        return "terminos";
    }

    @GetMapping("/terminos.html")
    public String terminosHtml() {
        return "terminos";
    }
}