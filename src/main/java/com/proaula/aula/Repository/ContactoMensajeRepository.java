package com.proaula.aula.Repository;

import com.proaula.aula.document.ContactoMensaje;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactoMensajeRepository extends MongoRepository<ContactoMensaje, String> {
    List<ContactoMensaje> findByNombreOrEmail(String nombre, String email);
}