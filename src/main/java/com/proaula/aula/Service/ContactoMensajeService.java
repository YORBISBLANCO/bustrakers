package com.proaula.aula.Service;

import com.proaula.aula.document.ContactoMensaje;
import com.proaula.aula.Repository.mongodb.ContactoMensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactoMensajeService {
    @Autowired
    private ContactoMensajeRepository contactoMensajeRepository;

    public ContactoMensaje guardarMensaje(ContactoMensaje mensaje) {
        return contactoMensajeRepository.save(mensaje);
    }

    public List<ContactoMensaje> getAllMensajes() {
        return contactoMensajeRepository.findAll();
    }

    public ContactoMensaje getMensajeById(String id) {
        return contactoMensajeRepository.findById(id).orElse(null);
    }

    public void deleteMensaje(String id) {
        contactoMensajeRepository.deleteById(id);
    }
    
    public long count() {
        return contactoMensajeRepository.count();
    }
    
}