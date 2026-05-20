package com.proaula.aula.Repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.proaula.aula.document.Ruta;
import java.util.Optional;
import java.util.List;

@Repository
public interface RutaRepository extends MongoRepository<Ruta, String> {
    Optional<Ruta> findByNombre(String nombre);
    List<Ruta> findByNombreContainingIgnoreCase(String nombre);
    List<Ruta> findByBarriosContaining(String barrio);
    List<Ruta> findByActivaTrue();
}
