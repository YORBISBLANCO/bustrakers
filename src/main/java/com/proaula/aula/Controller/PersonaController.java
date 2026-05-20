package com.proaula.aula.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.proaula.aula.document.Persona;


@RestController
@RequestMapping("/personas")
public class PersonaController {

    @Autowired
    private com.proaula.aula.Repository.mongodb.PersonaRepository personaRepository;

    // Guardar una persona
    @PostMapping
    public Persona guardar(@RequestBody Persona persona) {
        return personaRepository.save(persona);
    }

    // Listar todas las personas
    @GetMapping
    public List<Persona> listar() {
        return personaRepository.findAll();
    }
}
