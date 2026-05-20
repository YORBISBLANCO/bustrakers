package com.proaula.aula.Repository;

import com.proaula.aula.document.Ruta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRepository extends MongoRepository<Ruta, String> {
    // Operaciones CRUD automáticas
    
    
}