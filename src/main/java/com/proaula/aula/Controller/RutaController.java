package com.proaula.aula.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.proaula.aula.document.Bus;
import com.proaula.aula.document.Parada;
import com.proaula.aula.document.Ruta;
import com.proaula.aula.Service.BusService;
import com.proaula.aula.Service.ParadaService;
import com.proaula.aula.Service.RutaService;

@Controller
public class RutaController {
    @Autowired
    private RutaService rutaService;

    @Autowired
    private BusService busService;

    @Autowired
    private ParadaService paradaService;
    
    // Vista para editar rutas
    @GetMapping("/editar-ruta")
    public String mostrarEditarRuta(Model model) {
        model.addAttribute("rutas", rutaService.getAllRutas());
        return "Admin/editar_ruta";
    }

    @GetMapping("/editar-ruta/{id}")
    public String editarRutaPorId(@PathVariable String id, Model model) {
        Ruta ruta = rutaService.getRutaById(id);
        model.addAttribute("ruta", ruta);
        model.addAttribute("barrios", ruta != null ? ruta.getBarrios() : new ArrayList<>());
        model.addAttribute("rutas", rutaService.getAllRutas());
        return "Admin/editar_ruta";
    }

    @PostMapping("/editar-ruta/{id}")
    public String guardarEdicionRuta(@PathVariable String id, @ModelAttribute Ruta ruta, @RequestParam(required = false) String barrios) {
        ruta.setId(id);
        
        // Procesar barrios desde el textarea (string separado por newlines)
        if (barrios != null && !barrios.trim().isEmpty()) {
            List<String> barriosList = new ArrayList<>();
            String[] items = barrios.split("\n");
            for (String item : items) {
                String barrioLimpio = item.trim();
                if (!barrioLimpio.isEmpty()) {
                    barriosList.add(barrioLimpio);
                }
            }
            ruta.setBarrios(barriosList.isEmpty() ? null : barriosList);
        }
        
        rutaService.saveRuta(ruta);
        return "redirect:/editar-ruta";
    }

    // Vista pública para usuarios - Lista de rutas
    @GetMapping("/rutas")
    public String listarRutas(Model model,
                             @RequestParam(required = false) String buscar,
                             @RequestParam(required = false) String barrio) {
        if (buscar != null) {
            buscar = buscar.trim();
            if (buscar.isEmpty()) {
                buscar = null;
            }
        }

        List<Ruta> rutas = rutaService.getAllRutas();
        
        // Filtros
        if (buscar != null) {
            rutas = rutaService.findByNombreContaining(buscar);
        }
        
        if (barrio != null && !barrio.isEmpty()) {
            rutas = rutas.stream()
                .filter(r -> r.getBarrios() != null && r.getBarrios().contains(barrio))
                .collect(Collectors.toList());
        }
        
        // Obtener barrios únicos
        List<String> barrios = rutaService.getAllRutas().stream()
            .flatMap(r -> r.getBarrios() != null ? r.getBarrios().stream() : new ArrayList<String>().stream())
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        model.addAttribute("rutas", rutas);
        model.addAttribute("barriosDisponibles", barrios);
        model.addAttribute("buscar", buscar);
        model.addAttribute("barrioSeleccionado", barrio);
        
        return "rutas-lista";
    }
    
    // Vista de detalle de ruta
    @GetMapping("/ruta/{id}")
    public String detalleRuta(@PathVariable String id, Model model) {
        Ruta ruta = rutaService.getRutaById(id);
        if (ruta != null) {
            // Cargar buses y paradas reales para esta ruta
            // Primero intenta usar datos embebidos, sino busca en colecciones separadas
            if (ruta.getBuses() == null || ruta.getBuses().isEmpty()) {
                List<Bus> buses = busService.getBusesByRutaId(id);
                if (!buses.isEmpty()) {
                    ruta.setBuses(buses);
                }
            }
            if (ruta.getParadas() == null || ruta.getParadas().isEmpty()) {
                List<Parada> paradas = paradaService.getParadasByRutaId(id);
                if (!paradas.isEmpty()) {
                    ruta.setParadas(paradas);
                }
            }
            model.addAttribute("ruta", ruta);
            return "detalle-ruta";
        }
        return "redirect:/rutas";
    }

    @GetMapping("/reservar/ruta/{id}")
    public String reservarRuta(@PathVariable String id, Model model) {
        Ruta ruta = rutaService.getRutaById(id);
        if (ruta == null) {
            return "redirect:/rutas";
        }

        // Cargar datos de ruta, priorizando datos embebidos
        if (ruta.getBuses() == null || ruta.getBuses().isEmpty()) {
            List<Bus> buses = busService.getBusesByRutaId(id);
            if (!buses.isEmpty()) {
                ruta.setBuses(buses);
            }
        }
        if (ruta.getParadas() == null || ruta.getParadas().isEmpty()) {
            List<Parada> paradas = paradaService.getParadasByRutaId(id);
            if (!paradas.isEmpty()) {
                ruta.setParadas(paradas);
            }
        }

        model.addAttribute("ruta", ruta);
        model.addAttribute("rutaId", id);
        return "Usuario/viajar";
    }

    // Páginas de gestión de rutas (apuntan a plantillas en Admin si aplica)
    @GetMapping({"/rutas/admin", "/agregar_rutas"})
    public String gestionarRutas(Model model) {
        List<Ruta> rutas = rutaService.getAllRutas();
        // Formatear la hora para cada ruta
        List<String> horasFormateadas = new ArrayList<>();
        for (Ruta ruta : rutas) {
            if (ruta.getHoraAproximada() != null) {
                horasFormateadas.add(ruta.getHoraAproximada().toString().substring(0,5));
            } else {
                horasFormateadas.add("");
            }
        }
        model.addAttribute("rutas", rutas);
        model.addAttribute("horasFormateadas", horasFormateadas);
        model.addAttribute("newRuta", new Ruta());
        return "Admin/agregar_rutas";
    }

    @PostMapping("/rutas")
    public String agregarRuta(@ModelAttribute Ruta ruta, @RequestParam(required = false) String barrios) {
        if (barrios != null && !barrios.trim().isEmpty()) {
            ruta.setBarrios(List.of(barrios));
        }
        procesarBarrios(ruta);
        rutaService.saveRuta(ruta);
        return "redirect:/agregar_rutas";
    }

    @GetMapping("/eliminar-ruta/{id}")
    public String eliminarRuta(@PathVariable String id) {
        rutaService.deleteRuta(id);
        return "redirect:/agregar_rutas";
    }

    /**
     * Método auxiliar para procesar los barrios
     * Maneja cadenas separadas por coma o newline, o listas
     */
    private void procesarBarrios(Ruta ruta) {
        if (ruta.getBarrios() != null && !ruta.getBarrios().isEmpty()) {
            List<String> barriosProcesados = new ArrayList<>();
            for (String barrio : ruta.getBarrios()) {
                if (barrio != null && !barrio.trim().isEmpty()) {
                    // Dividir por newlines o comas
                    String[] items = barrio.split("[\n,]");
                    for (String item : items) {
                        String barrioLimpio = item.trim();
                        if (!barrioLimpio.isEmpty()) {
                            barriosProcesados.add(barrioLimpio);
                        }
                    }
                }
            }
            ruta.setBarrios(barriosProcesados.isEmpty() ? null : barriosProcesados);
        }
    }

    // Endpoint de debugging para ver qué datos existen para una ruta
    @GetMapping("/debug/ruta/{id}")
    public String debugRuta(@PathVariable String id, Model model) {
        Ruta ruta = rutaService.getRutaById(id);
        
        if (ruta != null) {
            // Información de la ruta
            List<Bus> busesEmbebidos = ruta.getBuses();
            List<Parada> paradasEmbebidas = ruta.getParadas();
            
            // Información de colecciones separadas
            List<Bus> busesSeparados = busService.getBusesByRutaId(id);
            List<Parada> paradasSeparadas = paradaService.getParadasByRutaId(id);
            
            model.addAttribute("ruta", ruta);
            model.addAttribute("busesEmbebidos", busesEmbebidos);
            model.addAttribute("paradasEmbebidas", paradasEmbebidas);
            model.addAttribute("busesSeparados", busesSeparados);
            model.addAttribute("paradasSeparadas", paradasSeparadas);
            
            System.out.println("=== DEBUG RUTA: " + ruta.getNombre() + " ===");
            System.out.println("ID: " + id);
            System.out.println("Buses Embebidos: " + (busesEmbebidos != null ? busesEmbebidos.size() : 0));
            System.out.println("Paradas Embebidas: " + (paradasEmbebidas != null ? paradasEmbebidas.size() : 0));
            System.out.println("Buses Separados (por rutaId): " + busesSeparados.size());
            System.out.println("Paradas Separadas (por rutaId): " + paradasSeparadas.size());
        }
        
        return "debug-ruta";
    }
}
