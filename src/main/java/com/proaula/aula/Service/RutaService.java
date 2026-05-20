package com.proaula.aula.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.proaula.aula.document.Ruta;
import com.proaula.aula.Repository.mongodb.RutaRepository;

@Service
public class RutaService {
    @Autowired
    private RutaRepository rutaRepository;

    public List<Ruta> getAllRutas() {
        return rutaRepository.findAll();
    }

    public Ruta getRutaById(String id) {
        return rutaRepository.findById(id).orElse(null);
    }

    public Ruta saveRuta(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    public void deleteRuta(String id) {
        rutaRepository.deleteById(id);
    }

    public void deleteAllRutas() {
        rutaRepository.deleteAll();
    }

    public long count() {
        return rutaRepository.count();
    }

    public List<Ruta> findTop4ByOrderByNombre() {
        return rutaRepository.findAll().stream()
            .limit(4)
            .collect(Collectors.toList());
    }

    public long countActiveBuses() {
        // Implementar conteo de buses activos desde la colección de buses
        return 0; // Implementar según necesidad
    }

    public List<Ruta> findByBarrio(String barrio) {
        return rutaRepository.findByBarriosContaining(barrio);
    }

    public List<Ruta> findByNombreContaining(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return rutaRepository.findAll();
        }
        String texto = nombre.trim();
        return rutaRepository.findByNombreContainingIgnoreCase(texto);
    }

    public Page<Ruta> findAllPaginated(Pageable pageable) {
        List<Ruta> rutas = rutaRepository.findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), rutas.size());
        List<Ruta> pageContent = rutas.subList(start, end);
        return new PageImpl<>(pageContent, pageable, rutas.size());
    }

    public Optional<Ruta> findById(String id) {
        return rutaRepository.findById(id);
    }
}