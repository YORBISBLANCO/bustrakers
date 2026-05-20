package com.proaula.aula.Repository.mongodb;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.proaula.aula.document.AuditoriaLog;

@Repository
public interface AuditoriaLogRepository extends MongoRepository<AuditoriaLog, String> {
    
    // Busca por usuario
    List<AuditoriaLog> findByUsuarioOrderByFechaDesc(String usuario);
    
    // Busca por entidad
    List<AuditoriaLog> findByIdEntidadOrderByFechaDesc(String idEntidad);
    
    // Busca por tipo de cambio
    List<AuditoriaLog> findByTipoOrderByFechaDesc(String tipo);
    
    // Busca antes de una fecha (para limpiar)
    List<AuditoriaLog> findByFechaBeforeOrderByFechaDesc(LocalDateTime fecha);
}
