package com.proaula.aula.Repository;

import com.proaula.aula.document.Parada;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParadaRepository extends MongoRepository<Parada, String> {
    // Operaciones CRUD automáticas
}