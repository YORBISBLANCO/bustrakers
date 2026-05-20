package com.proaula.aula.Repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.proaula.aula.document.Barrio;
import java.util.Optional;
import java.util.List;

@Repository
public interface BarrioRepository extends MongoRepository<Barrio, String> {
    Optional<Barrio> findByNombre(String nombre);
    List<Barrio> findByLocalidad(String localidad);
    List<Barrio> findByActivoTrue();
}
