package com.proaula.aula.Repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.proaula.aula.document.Persona;

public interface PersonaRepository extends MongoRepository<Persona, String> {
}
