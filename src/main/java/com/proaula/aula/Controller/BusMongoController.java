package com.proaula.aula.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.proaula.aula.document.Bus;
import com.proaula.aula.Repository.mongodb.BusMongoRepository;

@RestController
@RequestMapping("/api/buses-mongo")
public class BusMongoController {

    @Autowired
    private BusMongoRepository busMongoRepository;

    // Listar todos los buses de MongoDB
    @GetMapping
    public List<Bus> listarBuses() {
        return busMongoRepository.findAll();
    }

    // Guardar un nuevo bus
    @PostMapping
    public Bus guardarBus(@RequestBody Bus bus) {
        return busMongoRepository.save(bus);
    }

    // Obtener un bus por ID
    @GetMapping("/{id}")
    public Bus obtenerBus(@PathVariable String id) {
        return busMongoRepository.findById(id).orElse(null);
    }

    // Eliminar un bus
    @DeleteMapping("/{id}")
    public void eliminarBus(@PathVariable String id) {
        busMongoRepository.deleteById(id);
    }
}
