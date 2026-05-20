package com.proaula.aula.Repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.proaula.aula.document.Bus;

@Repository
public interface BusMongoRepository extends MongoRepository<Bus, String> {

}
