package com.proaula.aula.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proaula.aula.document.AuditoriaLog;
import com.proaula.aula.Repository.mongodb.AuditoriaLogRepository;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaLogRepository auditoriaLogRepository;

    
    public AuditoriaLog registrarCambio(String tipo, String descripcion, String usuario, String idEntidad) {
        AuditoriaLog log = new AuditoriaLog();
        log.setTipo(tipo);
        log.setDescripcion(descripcion);
        log.setUsuario(usuario);
        log.setIdEntidad(idEntidad);
        log.setFecha(LocalDateTime.now());
        log.setIp(obtenerIPCliente());

        return auditoriaLogRepository.save(log);
    }

   
    public void registrarCreacion(String nombreEntidad, String idEntidad, String usuario) {
        registrarCambio("CREACION", "Se creó " + nombreEntidad, usuario, idEntidad);
    }

    
    public void registrarActualizacion(String nombreEntidad, String idEntidad, String usuario, String cambios) {
        registrarCambio("ACTUALIZACION", "Se actualizó " + nombreEntidad + ": " + cambios, usuario, idEntidad);
    }

    
    public void registrarEliminacion(String nombreEntidad, String idEntidad, String usuario) {
        registrarCambio("ELIMINACION", "Se eliminó " + nombreEntidad, usuario, idEntidad);
    }

    
    public List<AuditoriaLog> obtenerHistorialUsuario(String usuario) {
        return auditoriaLogRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    public List<AuditoriaLog> obtenerHistorialEntidad(String idEntidad) {
        return auditoriaLogRepository.findByIdEntidadOrderByFechaDesc(idEntidad);
    }

    public List<AuditoriaLog> obtenerCambiosPorTipo(String tipo) {
        return auditoriaLogRepository.findByTipoOrderByFechaDesc(tipo);
    }

    
    public List<AuditoriaLog> obtenerHistorialReciente(int limite) {
        return new ArrayList<>(auditoriaLogRepository.findAll())
            .stream()
            .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
            .limit(limite)
            .toList();
    }

    
    public java.util.Map<String, Object> obtenerEstadisticas() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        List<AuditoriaLog> todos = auditoriaLogRepository.findAll();

        stats.put("totalEventos", todos.size());
        stats.put("creaciones", todos.stream().filter(a -> a.getTipo().equals("CREACION")).count());
        stats.put("actualizaciones", todos.stream().filter(a -> a.getTipo().equals("ACTUALIZACION")).count());
        stats.put("eliminaciones", todos.stream().filter(a -> a.getTipo().equals("ELIMINACION")).count());
        stats.put("ultimoEvento", todos.isEmpty() ? null : todos.get(0).getFecha());

        return stats;
    }

    
    public int limpiarHistorial(LocalDateTime antes) {
        List<AuditoriaLog> aLimpiar = auditoriaLogRepository.findByFechaBeforeOrderByFechaDesc(antes);
        auditoriaLogRepository.deleteAll(aLimpiar);
        return aLimpiar.size();
    }

    private String obtenerIPCliente() {
        // TODO: Implementar obtención de IP real del cliente
        return "127.0.0.1";
    }
}
