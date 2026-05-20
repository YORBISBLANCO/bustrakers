package com.proaula.aula.Repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.proaula.aula.document.Parada;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParadaRepository extends MongoRepository<Parada, String> {
    Optional<Parada> findByNombre(String nombre);
    List<Parada> findByRutaId(String rutaId);
    List<Parada> findByRutaNombreOrderByOrdenAsc(String rutaNombre);
    List<Parada> findByActivaTrue();
}
