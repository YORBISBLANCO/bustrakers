package com.proaula.aula.Repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.proaula.aula.document.ContactoMensaje;
import java.util.List;

@Repository
public interface ContactoMensajeRepository extends MongoRepository<ContactoMensaje, String> {
    List<ContactoMensaje> findByLeidoFalse();
    List<ContactoMensaje> findByEmail(String email);
    List<ContactoMensaje> findByOrderByFechaCreacionDesc();
    List<ContactoMensaje> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCaseOrTelefonoContainingIgnoreCaseOrMensajeContainingIgnoreCase(String nombre, String email, String telefono, String mensaje);
}
