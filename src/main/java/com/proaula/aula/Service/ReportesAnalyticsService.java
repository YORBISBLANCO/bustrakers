package com.proaula.aula.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proaula.aula.document.Bus;
import com.proaula.aula.document.Ruta;
import com.proaula.aula.document.Usuario;
import com.proaula.aula.Repository.mongodb.BusRepository;
import com.proaula.aula.Repository.mongodb.RutaRepository;
import com.proaula.aula.Repository.mongodb.UsuarioRepository;
import com.proaula.aula.Repository.mongodb.BusMongoRepository;
import com.proaula.aula.Repository.mongodb.AuditoriaLogRepository;

@Service
public class ReportesAnalyticsService {

    @Autowired
    private BusRepository busRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BusMongoRepository busMongoRepository;

    @Autowired
    private AuditoriaLogRepository auditoriaLogRepository;

    
    public Map<String, Object> generarReporteGeneral() {
        Map<String, Object> reporte = new HashMap<>();

        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalUsuarios", usuarioRepository.count());
        estadisticas.put("totalRutas", rutaRepository.count());
        estadisticas.put("totalBusesMySQL", busRepository.count());
        estadisticas.put("totalBusesMongoDB", busMongoRepository.count());
        estadisticas.put("totalBusesGlobal", busRepository.count() + busMongoRepository.count());

        reporte.put("estadisticas", estadisticas);
        reporte.put("timestamp", LocalDateTime.now());
        
        Map<String, Object> detalles = new HashMap<>();
        detalles.put("usuarios", obtenerDetallesUsuarios());
        detalles.put("rutas", obtenerDetallesRutas());
        detalles.put("buses", obtenerDetallesBuses());
        reporte.put("detalles", detalles);

        return reporte;
    }

    
    public Map<String, Object> obtenerDetallesUsuarios() {
        Map<String, Object> detalles = new HashMap<>();
        List<Usuario> usuarios = usuarioRepository.findAll();

        detalles.put("total", usuarios.size());
        detalles.put("administradores", usuarios.stream()
            .filter(u -> u.getRole().equals("ROLE_ADMIN")).count());
        detalles.put("usuarios_regulares", usuarios.stream()
            .filter(u -> u.getRole().equals("ROLE_USER")).count());

        
        detalles.put("usuarios_recientes", usuarios.stream().limit(5)
            .map(u -> Map.of(
                "username", u.getUsername(),
                "email", u.getEmail(),
                "nombre_completo", u.getNombres() + " " + u.getApellidos()))
            .collect(Collectors.toList()));

        return detalles;
    }

   
    public Map<String, Object> obtenerDetallesRutas() {
        Map<String, Object> detalles = new HashMap<>();
        List<Ruta> rutas = rutaRepository.findAll();

        detalles.put("total", rutas.size());
        detalles.put("ruta_con_mas_buses", rutas.stream()
            .max((r1, r2) -> Integer.compare(0, 0)) // En MongoDB no hay relación directa
            .map(r -> Map.of(
                "id", r.getId(),
                "nombre", r.getNombre(),
                "num_buses", 0))
            .orElse(null));

        
        detalles.put("rutas_populares", rutas.stream()
            .filter(r -> r.isActiva()) // Usar el campo activa en lugar de buses
            .map(r -> Map.of(
                "nombre", r.getNombre(),
                "num_buses", 0, // No hay relación directa en MongoDB
                "barrios", r.getBarrios()))
            .collect(Collectors.toList()));

        return detalles;
    }

    
    public Map<String, Object> obtenerDetallesBuses() {
        Map<String, Object> detalles = new HashMap<>();

        List<Bus> busesMySQL = busRepository.findAll();
        List<com.proaula.aula.document.Bus> busesMongoDB = busMongoRepository.findAll();

        
        Map<String, Long> coloresComunes = busesMySQL.stream()
            .collect(Collectors.groupingBy(Bus::getColor, Collectors.counting()));

        detalles.put("total_mysql", busesMySQL.size());
        detalles.put("total_mongodb", busesMongoDB.size());
        detalles.put("colores_populares", coloresComunes.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(5)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        
        Map<String, Long> conductores = busesMySQL.stream()
            .collect(Collectors.groupingBy(Bus::getConductor, Collectors.counting()));

        detalles.put("conductores_activos", conductores.size());
        detalles.put("conductores_top", conductores.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(10)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return detalles;
    }

   
    public Map<String, Object> generarReporteAuditoria() {
        Map<String, Object> reporte = new HashMap<>();

        List<com.proaula.aula.document.AuditoriaLog> logs = auditoriaLogRepository.findAll();

        reporte.put("total_eventos", logs.size());
        reporte.put("eventos_por_tipo", logs.stream()
            .collect(Collectors.groupingBy(
                com.proaula.aula.document.AuditoriaLog::getTipo,
                Collectors.counting())));

        reporte.put("usuarios_activos", logs.stream()
            .map(com.proaula.aula.document.AuditoriaLog::getUsuario)
            .distinct()
            .collect(Collectors.toList()));

        reporte.put("eventos_recientes", logs.stream()
            .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
            .limit(20)
            .collect(Collectors.toList()));

        return reporte;
    }

    /**
     * Reporte de actividad por usuario
     */
    public Map<String, Object> generarReporteUsuario(String usuario) {
        Map<String, Object> reporte = new HashMap<>();

        Usuario usuarioObj = usuarioRepository.findAll().stream()
            .filter(u -> u.getUsername().equals(usuario))
            .findFirst()
            .orElse(null);

        if (usuarioObj != null) {
            reporte.put("usuario", Map.of(
                "username", usuarioObj.getUsername(),
                "email", usuarioObj.getEmail(),
                "nombre_completo", usuarioObj.getNombres() + " " + usuarioObj.getApellidos(),
                "role", usuarioObj.getRole()));

            List<com.proaula.aula.document.AuditoriaLog> actividad = 
                auditoriaLogRepository.findByUsuarioOrderByFechaDesc(usuario);

            reporte.put("total_acciones", actividad.size());
            reporte.put("tipos_acciones", actividad.stream()
                .collect(Collectors.groupingBy(
                    com.proaula.aula.document.AuditoriaLog::getTipo,
                    Collectors.counting())));
            reporte.put("acciones_recientes", actividad.stream().limit(10).collect(Collectors.toList()));
        }

        return reporte;
    }

    /**
     * Gráficos/estadísticas para dashboard
     */
    public Map<String, Object> generarEstadisticasDashboard() {
        Map<String, Object> stats = new HashMap<>();

        // Distribución de buses
        Map<String, Object> distribucionBuses = new HashMap<>();
        distribucionBuses.put("mysql", busRepository.count());
        distribucionBuses.put("mongodb", busMongoRepository.count());
        stats.put("distribucion_buses", distribucionBuses);

        // Crecimiento de usuarios (simulado)
        stats.put("crecimiento_usuarios", Map.of(
            "total", usuarioRepository.count(),
            "este_mes", usuarioRepository.count() * 0.1,
            "porcentaje_crecimiento", 10));

        // Rutas más activas
        List<Ruta> rutasActivas = rutaRepository.findAll().stream()
            .filter(r -> r.isActiva()) // Filtrar por rutas activas en lugar de buses
            .sorted((r1, r2) -> Integer.compare(0, 0)) // Sin orden específico en MongoDB
            .limit(5)
            .collect(Collectors.toList());

        stats.put("rutas_top_5", rutasActivas.stream()
            .map(r -> Map.of(
                "nombre", r.getNombre(),
                "buses", 0)) // No hay relación directa en MongoDB
            .collect(Collectors.toList()));

        stats.put("timestamp", LocalDateTime.now());

        return stats;
    }
}
