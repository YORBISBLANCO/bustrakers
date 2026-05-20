package com.proaula.aula.Repository;

import com.proaula.aula.document.Barrio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BarrioRepository extends MongoRepository<Barrio, String> {
    Optional<Barrio> findByNombre(String nombre);
    List<Barrio> findByLocalidad(String localidad);
    boolean existsByNombre(String nombre);
}
