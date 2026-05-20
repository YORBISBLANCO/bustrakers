package com.proaula.aula.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.proaula.aula.document.AdminCode;

@Repository
public interface AdminCodeRepository extends MongoRepository<AdminCode, String> {
    Optional<AdminCode> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
