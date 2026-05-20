package com.proaula.aula.Repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.proaula.aula.document.Bus;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends MongoRepository<Bus, String> {
    Optional<Bus> findByPlaca(String placa);
    List<Bus> findByRutaId(String rutaId);
    List<Bus> findByRutaNombre(String rutaNombre);
    List<Bus> findByActivoTrue();
    List<Bus> findByConductor(String conductor);
}
